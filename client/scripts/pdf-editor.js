/**
 * PDF Editor Module - Handles PDF rotation and renaming operations
 * Uses pdf-lib for PDF manipulation and jspdf for additional operations
 */

// Initialize pdf-lib (assumes it's loaded via CDN in HTML)
const { PDFDocument, degrees } = PDFLib;

/**
 * Main function to process PDFs in a directory
 * @param {string} sourceDir - Source directory containing PDFs
 * @param {string} destDir - Destination directory for processed PDFs
 * @param {boolean} shouldRotate - Whether to rotate PDFs
 * @param {boolean} shouldRename - Whether to rename PDFs
 * @param {Object} renameMap - Object mapping old names to new names
 * @param {number} rotationAngle - Rotation angle in degrees (90, 180, 270)
 */
async function processDirectoryPDFs(sourceDir, destDir, shouldRotate = false, shouldRename = false, renameMap = {}, rotationAngle = 90) {
    try {
        const results = [];

        // Note: In a browser environment, you'll need to use File API or server endpoints
        // This example assumes you have a way to get files from the directory
        const files = await getFilesFromDirectory(sourceDir);

        for (const file of files) {
            if (file.name.toLowerCase().endsWith('.pdf')) {
                const result = await processSinglePDF(
                    file,
                    destDir,
                    shouldRotate,
                    shouldRename,
                    renameMap,
                    rotationAngle
                );
                results.push(result);
            }
        }

        return {
            success: true,
            processed: results.length,
            results: results
        };
    } catch (error) {
        console.error('Error processing directory:', error);
        return {
            success: false,
            error: error.message
        };
    }
}

/**
 * Process a single PDF file
 * @param {File|Blob} pdfFile - The PDF file to process
 * @param {string} destDir - Destination directory
 * @param {boolean} shouldRotate - Whether to rotate the PDF
 * @param {boolean} shouldRename - Whether to rename the PDF
 * @param {Object} renameMap - Mapping for renaming
 * @param {number} rotationAngle - Rotation angle
 */
async function processSinglePDF(pdfFile, destDir, shouldRotate, shouldRename, renameMap, rotationAngle) {
    try {
        // Read the PDF file
        const arrayBuffer = await pdfFile.arrayBuffer();
        const pdfDoc = await PDFDocument.load(arrayBuffer);

        let modified = false;

        // Rotate if needed
        if (shouldRotate) {
            rotatePDF(pdfDoc, rotationAngle);
            modified = true;
        }

        // Determine new filename
        let newFilename = pdfFile.name;
        if (shouldRename) {
            newFilename = getNewFilename(pdfFile.name, renameMap);
        }

        // Save the PDF
        const pdfBytes = await pdfDoc.save();

        // Create result object
        return {
            originalName: pdfFile.name,
            newName: newFilename,
            rotated: shouldRotate,
            rotationAngle: shouldRotate ? rotationAngle : 0,
            size: pdfBytes.length,
            pageCount: pdfDoc.getPageCount(),
            pdfBytes: pdfBytes
        };

    } catch (error) {
        console.error(`Error processing PDF ${pdfFile.name}:`, error);
        return {
            originalName: pdfFile.name,
            error: error.message
        };
    }
}

/**
 * Rotate all pages in a PDF document
 * @param {PDFDocument} pdfDoc - The PDF document to rotate
 * @param {number} angle - Rotation angle in degrees
 */
function rotatePDF(pdfDoc, angle) {
    const pages = pdfDoc.getPages();
    pages.forEach(page => {
        // Get current rotation
        const currentRotation = page.getRotation().angle;
        // Set new rotation
        page.setRotation(degrees(currentRotation + angle));
    });
}

/**
 * Get new filename based on rename mapping
 * @param {string} originalName - Original filename
 * @param {Object} renameMap - Mapping object or crosswalk data
 */
function getNewFilename(originalName, renameMap) {
    // Remove extension for matching
    const nameWithoutExt = originalName.replace(/\.pdf$/i, '');

    // Check if there's a direct mapping
    if (renameMap[nameWithoutExt]) {
        return renameMap[nameWithoutExt] + '.pdf';
    }

    // Check if original name contains the key (partial matching)
    for (const [key, value] of Object.entries(renameMap)) {
        if (nameWithoutExt.includes(key)) {
            return nameWithoutExt.replace(key, value) + '.pdf';
        }
    }

    // If no mapping found, return original name
    return originalName;
}

/**
 * Parse crosswalk string into rename mapping object
 * @param {string} crosswalkStr - String in format "oldName1:newName1\noldName2:newName2"
 */
function parseCrosswalk(crosswalkStr) {
    const mapping = {};
    const lines = crosswalkStr.split('\n');

    lines.forEach(line => {
        const parts = line.trim().split(':');
        if (parts.length === 2) {
            mapping[parts[0].trim()] = parts[1].trim();
        }
    });

    return mapping;
}

/**
 * Parse CSV file into rename mapping object
 * Supports multiple formats: CSV with/without headers, colon-separated values
 * @param {string} csvContent - CSV file content
 * @returns {Object} Mapping object {oldName: newName}
 */
function parseCSVCrosswalk(csvContent) {
    const mapping = {};
    const lines = csvContent.split('\n');

    lines.forEach((line, index) => {
        // Skip empty lines
        if (!line.trim()) return;

        // Try to detect delimiter (comma or colon)
        let parts;
        if (line.includes(',')) {
            // CSV format: oldName,newName
            parts = line.split(',').map(part => part.trim().replace(/^["']|["']$/g, ''));
        } else if (line.includes(':')) {
            // Colon format: oldName:newName
            parts = line.split(':').map(part => part.trim());
        } else {
            // Skip lines that don't have a recognizable delimiter
            return;
        }

        // Skip header row if it looks like a header
        if (index === 0) {
            const firstCell = parts[0].toLowerCase();
            if (firstCell === 'old' || firstCell === 'oldname' ||
                firstCell === 'original' || firstCell === 'from' ||
                firstCell === 'source') {
                return; // Skip header row
            }
        }

        // Extract old and new names
        if (parts.length >= 2) {
            const oldName = parts[0].trim();
            const newName = parts[1].trim();

            if (oldName && newName) {
                // Remove .pdf extension if present
                const cleanOldName = oldName.replace(/\.pdf$/i, '');
                const cleanNewName = newName.replace(/\.pdf$/i, '');

                mapping[cleanOldName] = cleanNewName;
            }
        }
    });

    return mapping;
}

/**
 * Read and parse a CSV file
 * @param {File} file - CSV file to parse
 * @returns {Promise<Object>} Promise resolving to mapping object
 */
async function readCSVFile(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();

        reader.onload = function(e) {
            try {
                const content = e.target.result;
                const mapping = parseCSVCrosswalk(content);
                resolve(mapping);
            } catch (error) {
                reject(error);
            }
        };

        reader.onerror = function() {
            reject(new Error('Failed to read CSV file'));
        };

        reader.readAsText(file);
    });
}

/**
 * Batch rotate PDFs with specific angle
 * @param {FileList|Array} files - List of PDF files
 * @param {number} angle - Rotation angle (90, 180, 270)
 */
async function batchRotatePDFs(files, angle = 90) {
    const results = [];

    for (const file of files) {
        if (file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf')) {
            try {
                const arrayBuffer = await file.arrayBuffer();
                const pdfDoc = await PDFDocument.load(arrayBuffer);

                // Rotate all pages
                rotatePDF(pdfDoc, angle);

                // Save rotated PDF
                const pdfBytes = await pdfDoc.save();

                results.push({
                    filename: file.name,
                    success: true,
                    pdfBytes: pdfBytes
                });
            } catch (error) {
                results.push({
                    filename: file.name,
                    success: false,
                    error: error.message
                });
            }
        }
    }

    return results;
}

/**
 * Batch rename PDFs based on mapping
 * @param {FileList|Array} files - List of PDF files
 * @param {Object|string} renameData - Rename mapping object or crosswalk string
 */
async function batchRenamePDFs(files, renameData) {
    const renameMap = typeof renameData === 'string' ? parseCrosswalk(renameData) : renameData;
    const results = [];

    for (const file of files) {
        if (file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf')) {
            const newName = getNewFilename(file.name, renameMap);

            results.push({
                originalName: file.name,
                newName: newName,
                renamed: file.name !== newName
            });
        }
    }

    return results;
}

/**
 * Combined operation: Rename and Rotate PDFs
 * @param {FileList|Array} files - List of PDF files
 * @param {Object} options - Options for processing
 */
async function processRenameAndRotate(files, options = {}) {
    const {
        rotate = false,
        rename = false,
        rotationAngle = 90,
        renameMap = {},
        crosswalk = ''
    } = options;

    const results = [];
    const mapping = crosswalk ? parseCrosswalk(crosswalk) : renameMap;

    for (const file of files) {
        if (file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf')) {
            try {
                const arrayBuffer = await file.arrayBuffer();
                const pdfDoc = await PDFDocument.load(arrayBuffer);

                // Apply rotation if needed
                if (rotate) {
                    rotatePDF(pdfDoc, rotationAngle);
                }

                // Determine new filename
                const newName = rename ? getNewFilename(file.name, mapping) : file.name;

                // Save the modified PDF
                const pdfBytes = await pdfDoc.save();

                // Create a blob for download
                const blob = new Blob([pdfBytes], { type: 'application/pdf' });

                results.push({
                    originalName: file.name,
                    newName: newName,
                    rotated: rotate,
                    renamed: rename && (file.name !== newName),
                    blob: blob,
                    url: URL.createObjectURL(blob)
                });

            } catch (error) {
                results.push({
                    originalName: file.name,
                    error: error.message
                });
            }
        }
    }

    return results;
}

/**
 * Helper function to download processed PDFs
 * @param {Array} results - Array of processed PDF results
 */
function downloadProcessedPDFs(results) {
    results.forEach(result => {
        if (result.blob && !result.error) {
            const link = document.createElement('a');
            link.href = result.url;
            link.download = result.newName || result.originalName;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

            // Clean up the URL
            setTimeout(() => URL.revokeObjectURL(result.url), 100);
        }
    });
}

/**
 * Extract PDF metadata using pdf-lib
 * @param {File|Blob} pdfFile - PDF file to analyze
 */
async function extractPDFMetadata(pdfFile) {
    try {
        const arrayBuffer = await pdfFile.arrayBuffer();
        const pdfDoc = await PDFDocument.load(arrayBuffer);

        return {
            pageCount: pdfDoc.getPageCount(),
            title: pdfDoc.getTitle(),
            author: pdfDoc.getAuthor(),
            subject: pdfDoc.getSubject(),
            creator: pdfDoc.getCreator(),
            producer: pdfDoc.getProducer(),
            creationDate: pdfDoc.getCreationDate(),
            modificationDate: pdfDoc.getModificationDate(),
            keywords: pdfDoc.getKeywords()
        };
    } catch (error) {
        console.error('Error extracting metadata:', error);
        return null;
    }
}

/**
 * Extract all text content from a PDF document
 * JavaScript equivalent of the Java getDocumentString method
 * @param {File|Blob|string} pdfSource - The PDF file, blob, or URL to extract text from
 * @returns {Promise<string|null>} The extracted text content, or null if extraction fails
 */
async function getDocumentString(pdfSource) {
    try {
        let arrayBuffer;

        // Handle different input types
        if (typeof pdfSource === 'string') {
            // It's a URL or file path
            arrayBuffer = await fetchPDFFromURL(pdfSource);
        } else if (pdfSource instanceof File || pdfSource instanceof Blob) {
            // It's a File or Blob object
            arrayBuffer = await pdfSource.arrayBuffer();
        } else {
            throw new Error('Invalid PDF source. Must be a File, Blob, or URL string.');
        }

        // Load the PDF document with PDF.js
        const loadingTask = pdfjsLib.getDocument({ data: arrayBuffer });
        const pdfDocument = await loadingTask.promise;

        // Check if document requires password (encrypted)
        // PDF.js will throw PasswordException if password is required

        let fullText = '';
        const numPages = pdfDocument.numPages;

        // Extract text from each page
        for (let pageNum = 1; pageNum <= numPages; pageNum++) {
            const page = await pdfDocument.getPage(pageNum);
            const textContent = await page.getTextContent();

            // Combine all text items from the page
            const pageText = textContent.items
                .map(item => item.str)
                .join(' ');

            fullText += pageText + '\n';
        }

        return fullText.trim();

    } catch (error) {
        if (error.name === 'PasswordException') {
            console.error('Encrypted PDFs are not supported.');
            return null;
        } else {
            console.error('Opening PDF failed!! ' + error.message);
            return null;
        }
    }
}

/**
 * Fetch PDF from a URL and return as ArrayBuffer
 * @param {string} url - URL of the PDF file
 * @returns {Promise<ArrayBuffer>} ArrayBuffer of the PDF
 */
async function fetchPDFFromURL(url) {
    try {
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const arrayBuffer = await response.arrayBuffer();
        return arrayBuffer;
    } catch (error) {
        console.error('Failed to fetch PDF from URL:', error);
        throw error;
    }
}

/**
 * Get PDF file from local file system using File System Access API
 * @param {string} filePath - Optional file path hint (not directly usable in browser)
 * @returns {Promise<File>} The selected PDF file
 */
async function getPDFFromFileSystem(filePath = null) {
    try {
        // Note: In browsers, we cannot directly access file paths due to security
        // Instead, we use File System Access API to prompt user to select file

        if (!('showOpenFilePicker' in window)) {
            throw new Error('File System Access API not supported. Please use file upload input instead.');
        }

        const [fileHandle] = await window.showOpenFilePicker({
            types: [{
                description: 'PDF Files',
                accept: {
                    'application/pdf': ['.pdf']
                }
            }],
            multiple: false
        });

        if (fileHandle) {
            const file = await fileHandle.getFile();
            return file;
        }

        return null;
    } catch (error) {
        if (error.name !== 'AbortError') {
            console.error('Error accessing file system:', error);
        }
        throw error;
    }
}

/**
 * Extract text from specific page of a PDF
 * @param {File|Blob|string} pdfSource - The PDF file, blob, or URL
 * @param {number} pageNumber - Page number (1-based index)
 * @returns {Promise<string|null>} The extracted text from the page
 */
async function getPageText(pdfSource, pageNumber) {
    try {
        let arrayBuffer;

        // Handle different input types
        if (typeof pdfSource === 'string') {
            arrayBuffer = await fetchPDFFromURL(pdfSource);
        } else if (pdfSource instanceof File || pdfSource instanceof Blob) {
            arrayBuffer = await pdfSource.arrayBuffer();
        } else {
            throw new Error('Invalid PDF source. Must be a File, Blob, or URL string.');
        }

        const loadingTask = pdfjsLib.getDocument({ data: arrayBuffer });
        const pdfDocument = await loadingTask.promise;

        if (pageNumber < 1 || pageNumber > pdfDocument.numPages) {
            throw new Error(`Invalid page number. Document has ${pdfDocument.numPages} pages.`);
        }

        const page = await pdfDocument.getPage(pageNumber);
        const textContent = await page.getTextContent();

        const pageText = textContent.items
            .map(item => item.str)
            .join(' ');

        return pageText.trim();

    } catch (error) {
        if (error.name === 'PasswordException') {
            console.error('Encrypted PDFs are not supported.');
            return null;
        } else {
            console.error('Extracting page text failed!! ' + error.message);
            return null;
        }
    }
}

/**
 * Get detailed text content with positioning information
 * Useful for advanced text extraction and layout analysis
 * @param {File|Blob|string} pdfSource - The PDF file, blob, or URL
 * @returns {Promise<Array|null>} Array of page objects with text items and positioning
 */
async function getDocumentTextWithPositions(pdfSource) {
    try {
        let arrayBuffer;

        // Handle different input types
        if (typeof pdfSource === 'string') {
            arrayBuffer = await fetchPDFFromURL(pdfSource);
        } else if (pdfSource instanceof File || pdfSource instanceof Blob) {
            arrayBuffer = await pdfSource.arrayBuffer();
        } else {
            throw new Error('Invalid PDF source. Must be a File, Blob, or URL string.');
        }

        const loadingTask = pdfjsLib.getDocument({ data: arrayBuffer });
        const pdfDocument = await loadingTask.promise;

        const pages = [];
        const numPages = pdfDocument.numPages;

        for (let pageNum = 1; pageNum <= numPages; pageNum++) {
            const page = await pdfDocument.getPage(pageNum);
            const textContent = await page.getTextContent();
            const viewport = page.getViewport({ scale: 1.0 });

            const pageData = {
                pageNumber: pageNum,
                width: viewport.width,
                height: viewport.height,
                items: textContent.items.map(item => ({
                    text: item.str,
                    x: item.transform[4],
                    y: item.transform[5],
                    width: item.width,
                    height: item.height,
                    fontName: item.fontName
                }))
            };

            pages.push(pageData);
        }

        return pages;

    } catch (error) {
        if (error.name === 'PasswordException') {
            console.error('Encrypted PDFs are not supported.');
            return null;
        } else {
            console.error('Extracting document text with positions failed!! ' + error.message);
            return null;
        }
    }
}

/**
 * Create PDF from images using jsPDF
 * @param {Array} images - Array of image files
 * @param {string} outputName - Name for the output PDF
 */
function createPDFFromImages(images, outputName = 'output.pdf') {
    // Initialize jsPDF
    const doc = new jsPDF();

    let firstPage = true;

    images.forEach((image, index) => {
        const reader = new FileReader();

        reader.onload = function(e) {
            if (!firstPage) {
                doc.addPage();
            }

            // Add image to PDF
            doc.addImage(e.target.result, 'JPEG', 10, 10, 190, 270);
            firstPage = false;

            // Save on last image
            if (index === images.length - 1) {
                doc.save(outputName);
            }
        };

        reader.readAsDataURL(image);
    });
}

// UI event handling is managed in main.js

/**
 * Display processing results in the UI
 * @param {Array} results - Processing results
 */
function displayResults(results) {
    const resultsDiv = document.getElementById('results');
    if (!resultsDiv) return;

    let html = '<h3>Processing Results:</h3>';
    html += '<div style="max-height: 400px; overflow-y: auto;">';

    results.forEach(result => {
        if (result.error) {
            html += `<div class="not-added">❌ ${result.originalName}: ${result.error}</div>`;
        } else {
            html += '<div class="added">';
            html += `✅ ${result.originalName}`;
            if (result.renamed) {
                html += ` → ${result.newName}`;
            }
            if (result.rotated) {
                html += ` (Rotated)`;
            }
            html += '</div>';
        }
    });

    html += '</div>';
    resultsDiv.innerHTML = html;
}

/**
 * Get files from a directory using File System Access API
 * @param {FileSystemDirectoryHandle} directoryHandle - Directory handle from showDirectoryPicker()
 * @returns {Array} Array of File objects
 */
async function getFilesFromDirectory(directoryHandle) {
    if (!directoryHandle) {
        console.error('No directory handle provided');
        return [];
    }

    const files = [];

    try {
        // Iterate through all entries in the directory
        for await (const entry of directoryHandle.values()) {
            if (entry.kind === 'file' && entry.name.toLowerCase().endsWith('.pdf')) {
                const fileHandle = await directoryHandle.getFileHandle(entry.name);
                const file = await fileHandle.getFile();
                files.push(file);
            }
        }
    } catch (error) {
        console.error('Error reading directory:', error);
    }

    return files;
}

/**
 * Save processed PDF to a destination directory
 * @param {FileSystemDirectoryHandle} destDirectoryHandle - Destination directory handle
 * @param {string} filename - Name for the file
 * @param {Uint8Array|Blob} pdfData - PDF data to save
 */
async function saveToDirectory(destDirectoryHandle, filename, pdfData) {
    try {
        // Create or get the file handle
        const fileHandle = await destDirectoryHandle.getFileHandle(filename, { create: true });

        // Create a writable stream
        const writable = await fileHandle.createWritable();

        // Write the PDF data
        if (pdfData instanceof Uint8Array) {
            await writable.write(pdfData);
        } else if (pdfData instanceof Blob) {
            await writable.write(await pdfData.arrayBuffer());
        } else {
            await writable.write(pdfData);
        }

        // Close the stream
        await writable.close();

        return { success: true, filename };
    } catch (error) {
        console.error(`Error saving file ${filename}:`, error);
        return { success: false, filename, error: error.message };
    }
}

/**
 * Check if File System Access API is supported
 */
function isFileSystemAccessSupported() {
    return 'showDirectoryPicker' in window;
}

/**
 * Show directory picker and return directory handle
 * @param {string} mode - 'read' or 'readwrite'
 */
async function pickDirectory(mode = 'read') {
    try {
        const options = {
            mode: mode
        };
        const directoryHandle = await window.showDirectoryPicker(options);
        return directoryHandle;
    } catch (error) {
        if (error.name !== 'AbortError') {
            console.error('Error picking directory:', error);
        }
        return null;
    }
}

/**
 * Extracts a "marketing name" from a document text, assuming a specific format.
 * It reads lines starting from the second line (index 1) until it encounters a line that
 * starts with "Coverage". It then processes the accumulated text.
 *
 * @param {string} documentText The multi-line string content.
 * @returns {string} The extracted marketing name string. Returns an empty string on error.
 */
function getPdfMarketingName(documentText) {
    try {
        // Splitting by newline to get lines, similar to Java's split("\n")
        const lines = documentText.split('\n');

        // Check for minimum line count; otherwise lines[1] access might fail
        if (lines.length <= 1) {
            return "";
        }

        let pdfMarketingName = "";
        let idx = 1;

        // Loop condition: starts at index 1 and continues as long as the current line
        // does NOT start with "Coverage".
        while (idx < lines.length && !lines[idx].startsWith("Coverage")) {
            // In JavaScript, we concatenate strings instead of using StringBuilder.
            // .replace(/\r/g, "") removes all carriage return characters (\r).
            pdfMarketingName += lines[idx].replace(/\r/g, "");

            // This original break condition seems redundant since the while loop condition
            // already checks for the start of "Coverage". If it's *contained* in the line,
            // the loop continues unless it *starts* with "Coverage" or we hit array end.
            // I'm keeping the logic as is for faithful translation, though simplified
            // JavaScript string concatenation makes the string builder part easier.
            if (lines[idx].includes("Coverage")) {
                break;
            }
            idx++;
        }

        // --- Post-processing Logic ---

        // Deletes from the start up to the index of ":" plus 2 (to remove ": " as well)
        // JavaScript's .indexOf() is similar to Java's. .substring() is used for deletion.
        const colonIndex = pdfMarketingName.indexOf(":");
        if (colonIndex !== -1) {
            // Keep everything AFTER the colon and the two characters that follow it
            pdfMarketingName = pdfMarketingName.substring(colonIndex + 2);
        }

        // If "Coverage" is found, delete everything from that index to the end.
        const coverageIndex = pdfMarketingName.indexOf("Coverage");
        if (coverageIndex !== -1) {
            // Keep everything BEFORE the "Coverage" string.
            // The Java equivalent was a bit ambiguous (deleting length - 1), but
            // usually, if you delete from an index, you want to delete to the end of the string.
            // The closest equivalent for removing 'Coverage' onwards is:
            pdfMarketingName = pdfMarketingName.substring(0, coverageIndex);
        }

        return pdfMarketingName;

    } catch (e) {
        // Standard error logging in JavaScript
        console.error("Error getting pdf Marketing Name!!", e.message);
        // Return an empty string, which is the equivalent of returning a new StringBuilder()
        return "";
    }
}

// Example usage (optional)
/*
const testDocument = "Header\nName: Product A\r\nDetail Line\nCoverage Information...";
const marketingName = getPdfMarketingName(testDocument);
console.log(marketingName); // Should output: "Product ADetail Line" (if 'Coverage' isn't on the next line)
*/

// Export functions for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        processDirectoryPDFs,
        processSinglePDF,
        batchRotatePDFs,
        batchRenamePDFs,
        processRenameAndRotate,
        parseCrosswalk,
        parseCSVCrosswalk,
        readCSVFile,
        extractPDFMetadata,
        getDocumentString,
        getPageText,
        getDocumentTextWithPositions,
        fetchPDFFromURL,
        getPDFFromFileSystem,
        createPDFFromImages,
        downloadProcessedPDFs,
        getFilesFromDirectory,
        saveToDirectory,
        isFileSystemAccessSupported,
        pickDirectory,
        getPdfMarketingName
    };
}