(function () {
    // check-marketing.js
    // Client-side PDF download + marketing name check with optional Directory Picker save

    // Elements
    const urlListEl = document.getElementById('cm-url-list');
    const mappingFileEl = document.getElementById('cm-mapping-file');
    const mappingDisplayEl = document.getElementById('cm-mapping-file-display');
    const pickMappingBtn = document.getElementById('cm-pick-mapping');
    const runBtn = document.getElementById('cm-run');
    const clearBtn = document.getElementById('cm-clear');
    const resultsEl = document.getElementById('cm-results');
    const resultsListEl = document.getElementById('cm-results-list');
    const totalEl = document.getElementById('cm-total');
    const matchesEl = document.getElementById('cm-matches');
    const failsEl = document.getElementById('cm-fails');
    const closeResultsBtn = document.getElementById('cm-close-results');
    const clearResultsBtn = document.getElementById('cm-clear-results');
    const downloadCheckbox = document.getElementById('cm-download-checkbox');
    const searchModeEl = document.getElementById('cm-search-mode');

    const pickDownloadBtn = document.getElementById('btn-pick-download-destination');
    const downloadDestinationInput = document.getElementById('download-destination');

    // In-memory directory handle (File System Access API). Can't reliably persist across sessions here.
    let downloadDirectoryHandle = null;

    // Wiring
    if (pickMappingBtn) pickMappingBtn.addEventListener('click', () => mappingFileEl.click());
    if (mappingFileEl) mappingFileEl.addEventListener('change', (e) => {
        const f = e.target.files[0];
        mappingDisplayEl.value = f ? f.name : '';
    });

    if (pickDownloadBtn) pickDownloadBtn.addEventListener('click', async () => {
        if (!window.showDirectoryPicker) {
            alert('Directory picker is not supported in this browser. Falling back to normal downloads.');
            return;
        }
        try {
            downloadDirectoryHandle = await window.showDirectoryPicker();
            // display chosen directory name
            downloadDestinationInput.value = downloadDirectoryHandle.name || 'Selected directory';
        } catch (err) {
            // user aborted or error
            console.debug('Directory pick cancelled or failed', err);
        }
    });

    if (clearBtn) clearBtn.addEventListener('click', () => {
        if (urlListEl) urlListEl.value = '';
        if (mappingFileEl) mappingFileEl.value = '';
        if (mappingDisplayEl) mappingDisplayEl.value = '';
        downloadDirectoryHandle = null;
        if (downloadDestinationInput) downloadDestinationInput.value = '';
    });

    if (closeResultsBtn) closeResultsBtn.addEventListener('click', () => { resultsEl.style.display = 'none'; });
    if (clearResultsBtn) clearResultsBtn.addEventListener('click', () => {
        resultsListEl.innerHTML = '';
        totalEl.textContent = '0';
        matchesEl.textContent = '0';
        failsEl.textContent = '0';
    });

    if (runBtn) runBtn.addEventListener('click', async () => {
        runBtn.disabled = true;
        resultsListEl.innerHTML = '';
        resultsEl.style.display = 'block';
        totalEl.textContent = '0';
        matchesEl.textContent = '0';
        failsEl.textContent = '0';

        const lambdaUrl = 'https://uhpkau3cwajzw634qlsfvqipsu0pnpjw.lambda-url.us-east-1.on.aws/';

        let mappings = await loadMappingsFromFile(mappingFileEl.files && mappingFileEl.files[0]);
        let urls = extractUrls(urlListEl.value || '');

        if (mappings.length) {
            // use mapping file's urls as canonical list
            urls = mappings.map(m => m.url);
        }

        const total = urls.length;
        totalEl.textContent = String(total);

        let matchCount = 0;
        let failCount = 0;

        // Process URLs in batches of 10
        const BATCH_SIZE = 10;
        for (let batchStart = 0; batchStart < urls.length; batchStart += BATCH_SIZE) {
            const batch = urls.slice(batchStart, batchStart + BATCH_SIZE);

            // Process batch concurrently
            await Promise.all(batch.map(async (url) => {
                const mapping = mappings.find(m => normalizeUrl(m.url) === normalizeUrl(url)) || {};
                // Use expected name from mapping, or extract from URL query parameter or filename
                const expected = mapping.expectedName || mapping.name || extractNameFromUrl(url);

                appendResultRow(url, 'Processing...', 'pending');
                try {
                    const arrayBuffer = await fetchPdfArrayBuffer(url, lambdaUrl);

                    // If download requested, trigger save
                    if (downloadCheckbox && downloadCheckbox.checked) {
                        try {
                            await savePdfToS3(url);
                        } catch (err) {
                            // fallback to normal anchor download
                            console.warn('Directory save failed, using fallback anchor download', err);
                        }
                    }

                    const extractedText = await extractTextFromPdfBuffer(arrayBuffer);
                    const found = findMarketingName(extractedText, searchModeEl ? searchModeEl.value : 'contains');
                    const match = compareNames(found, expected);
                    updateResultRow(url, match ? 'Match' : 'Mismatch', match ? 'success' : 'error', { expected, found });
                    if (match) matchCount++; else failCount++;
                } catch (err) {
                    updateResultRow(url, `Error: ${err.message || err}`, 'error');
                    failCount++;
                }

                matchesEl.textContent = String(matchCount);
                failsEl.textContent = String(failCount);
            }));

            // Small delay between batches to avoid overwhelming the system
            if (batchStart + BATCH_SIZE < urls.length) {
                await delay(300);
            }
        }

        runBtn.disabled = false;
    });

    // Helpers
    function extractUrls(text) {
        if (!text) return [];
        return text.split(/\r?\n/).map(s => s.trim()).filter(Boolean);
    }

    function normalizeUrl(u) {
        try { return new URL(u).toString(); } catch { return u; }
    }

    async function loadMappingsFromFile(file) {
        if (!file) return [];
        const ab = await readFileAsArrayBuffer(file);
        const name = (file.name || '').toLowerCase();
        if (name.endsWith('.csv')) {
            const csv = new TextDecoder('utf-8').decode(ab);
            return parseCsvMappings(csv);
        }
        try {
            const wb = XLSX.read(ab, { type: 'array' });
            const sheetName = wb.SheetNames[0];
            const json = XLSX.utils.sheet_to_json(wb.Sheets[sheetName], { defval: '' });
            return json.map(row => {
                const url = row.url || row.URL || row.link || row.Link || '';
                const expectedName = row.expectedName || row.expected_name || row.name || row.Name || '';
                return { url: String(url).trim(), expectedName: expectedName ? String(expectedName).trim() : '' };
            }).filter(r => r.url);
        } catch (e) {
            console.warn('Failed to parse XLSX mapping file', e);
            return [];
        }
    }

    function parseCsvMappings(csv) {
        const lines = csv.split(/\r?\n/).filter(Boolean);
        if (!lines.length) return [];
        const header = lines.shift().split(',').map(h => h.trim().toLowerCase());
        const urlIdx = header.findIndex(h => h === 'url' || h === 'link');
        const nameIdx = header.findIndex(h => h === 'expectedname' || h === 'name' || h === 'expected_name');
        const out = [];
        for (const line of lines) {
            const parts = line.split(',');
            const url = parts[urlIdx] ? parts[urlIdx].trim() : parts[0] ? parts[0].trim() : '';
            const expectedName = nameIdx >= 0 && parts[nameIdx] ? parts[nameIdx].trim() : '';
            if (url) out.push({ url, expectedName });
        }
        return out;
    }

    function readFileAsArrayBuffer(file) {
        return new Promise((res, rej) => {
            const fr = new FileReader();
            fr.onload = () => res(fr.result);
            fr.onerror = rej;
            fr.readAsArrayBuffer(file);
        });
    }

    async function fetchPdfArrayBuffer(url, lambdaUrl) {
        // Call Lambda function with the PDF URL
        const resp = await fetch(lambdaUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ url: url })
        });

        if (!resp.ok) throw new Error(`Lambda HTTP ${resp.status}`);

        const response = await resp.json();

        // Handle API Gateway proxy response format
        let data;
        if (response.body && typeof response.body === 'string') {
            // API Gateway proxy response - body is a JSON string
            data = JSON.parse(response.body);
        } else if (response.base64) {
            // Direct Lambda response
            data = response;
        } else {
            throw new Error('Invalid Lambda response format');
        }

        // Check for error response
        if (data.error) {
            throw new Error(`Lambda error: ${data.error}`);
        }

        // Decode base64 string to ArrayBuffer
        if (!data.base64) {
            throw new Error('Lambda response missing base64 field');
        }

        const base64String = data.base64;
        const binaryString = atob(base64String);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
        }
        return bytes.buffer;
    }

    function filenameFromUrl(url) {
        try {
            const u = new URL(url);
            const name = u.pathname.split('/').pop() || 'download.pdf';
            return decodeURIComponent(name);
        } catch {
            return 'download.pdf';
        }
    }

    async function savePdfToS3(pdfUrl) {
        const res = await fetch("https://v6rqm4q7fd27wkir5pax2w2fhi0rhmsf.lambda-url.us-east-1.on.aws/", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ pdfUrl })
        });

        const data = await res.json();
        console.log(data);
    }


    async function triggerDownload(arrayBuffer, filename) {
    }

    function triggerDownloadFallback(arrayBuffer, filename) {
    }

    async function extractTextFromPdfBuffer(arrayBuffer) {
        if (typeof pdfjsLib === 'undefined') throw new Error('pdfjsLib is not loaded');
        const loadingTask = pdfjsLib.getDocument({ data: arrayBuffer });
        const pdf = await loadingTask.promise;
        const maxPages = pdf.numPages;
        let fullText = '';
        for (let p = 1; p <= maxPages; p++) {
            const page = await pdf.getPage(p);
            const content = await page.getTextContent();
            const strings = content.items.map(i => i.str);
            fullText += strings.join(' ') + '\n';
        }
        return fullText;
    }

    function findMarketingName(text, mode) {
        if (!text) return '';
        if (mode === 'label') {
            const re = /Marketing\s*Name[:\-\s]*([^\n\r]+)/i;
            const m = text.match(re);
            if (m && m[1]) return m[1].trim();
        }
        const lines = text.split(/\r?\n/).map(l => l.trim()).filter(Boolean);
        for (const line of lines) {
            if (/marketing.*name/i.test(line)) {
                const sub = line.replace(/.*marketing.*name[:\-\s]*/i, '').trim();
                if (sub) return sub;
            }
        }
        // fallback: try to return first non-empty line that looks like a product name (heuristic)
        for (const line of lines) {
            if (line.length > 3 && /[A-Za-z0-9]/.test(line)) return line;
        }
        return '';
    }

    function compareNames(found, expected) {
        if (!expected && !found) return false;
        if (!expected && found) return true;
        if (!found && expected) return false;
        const f = String(found).toLowerCase();
        const e = String(expected).toLowerCase();
        return f.includes(e) || e.includes(f);
    }

    function extractNameFromUrl(url) {
        try {
            const u = new URL(url);
            const nameParam = u.searchParams.get('name') || u.searchParams.get('marketing') || u.searchParams.get('title');
            if (nameParam) return decodeURIComponent(nameParam);
            const seg = u.pathname.split('/').filter(Boolean).pop() || '';
            return seg.replace(/\.pdf$/i, '').replace(/[-_]/g, ' ');
        } catch {
            return '';
        }
    }

    function extractFilenameWithoutExtension(url) {
        try {
            const u = new URL(url);
            const filename = u.pathname.split('/').filter(Boolean).pop() || '';
            // Remove .pdf extension and decode URI component
            return decodeURIComponent(filename.replace(/\.pdf$/i, ''));
        } catch {
            return '';
        }
    }

    function appendResultRow(url, label, status) {
        const id = 'cm-' + Math.random().toString(36).slice(2, 9);
        const row = document.createElement('div');
        row.className = `result-row ${status || ''}`;
        row.dataset.url = url;
        row.id = id;
        row.innerHTML = `<div style="display:flex;justify-content:space-between;gap:8px;">
            <div style="flex:1;word-break:break-all">${escapeHtml(url)}</div>
            <div id="${id}-status" style="min-width:140px;text-align:right">${escapeHtml(label)}</div>
        </div>`;
        resultsListEl.appendChild(row);
        return id;
    }

    function updateResultRow(url, label, status, details) {
        const row = Array.from(resultsListEl.children).find(c => c.dataset.url === url);
        if (!row) return;
        row.className = `result-row ${status || ''}`;
        const statusEl = row.querySelector('[id$="-status"]');
        if (statusEl) statusEl.textContent = String(label);
        if (details) {
            const det = document.createElement('div');
            det.style.fontSize = '12px';
            det.style.marginTop = '6px';
            det.textContent = `Expected: ${details.expected || ''} | Found: ${details.found || ''}`;
            row.appendChild(det);
        }
    }

    function escapeHtml(s) {
        if (!s) return '';
        return s.replace(/[&<>"']/g, (m) => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));
    }

    function delay(ms) { return new Promise(res => setTimeout(res, ms)); }

})();

