// Set "Home" tab as the default active tab with welcome message
document.addEventListener('DOMContentLoaded', function() {
    // Hide all tab contents
    document.querySelectorAll('.tab-content').forEach(el => el.style.display = 'none');

    document.getElementById('rename-details').style.display = 'none';

    const defaultTab = document.getElementById('home-tab');
    defaultTab.style.display = 'block';

    const defaultButton = document.querySelector('.tab-button:first-child');
    defaultButton.classList.add('active');

    // Initialize event listeners for checkbox changes
    initializeEventListeners();
});

// Store processing results globally
let processingResults = [];

// Store directory handles
let sourceDirectoryHandle = null;
let destinationDirectoryHandle = null;

// Store crosswalk mapping from CSV
let crosswalkMapping = null;

// Store selected PDF file for viewing
let selectedPDFFile = null;

// Store selected marketing file
let selectedMarketingFile = null;

/**
 * Clear all results from the display
 */
function clearResults() {
    // Clear the results list HTML
    const resultsList = document.getElementById('results');
    if (resultsList) {
        resultsList.innerHTML = '';
    }

    // Reset all statistics to 0
    const totalFiles = document.getElementById('totalFiles');
    const successCount = document.getElementById('successCount');
    const errorCount = document.getElementById('errorCount');

    if (totalFiles) totalFiles.textContent = '0';
    if (successCount) successCount.textContent = '0';
    if (errorCount) errorCount.textContent = '0';

    // Clear the stored results array
    processingResults = [];

    // Hide the results container if it exists
    const resultsContainer = document.getElementById('results-container');
    if (resultsContainer) {
        resultsContainer.style.display = 'none';
    }

    // Show a temporary notification (optional)
    showNotification('Results cleared', 'info');
}

/**
 * Close the results panel
 */
function closeResults() {
    const resultsContainer = document.getElementById('results-container');
    if (resultsContainer) {
        resultsContainer.style.display = 'none';
    }
}

/**
 * Open a specific tab
 */
function openTab(event, tabName) {
    // Hide all tab contents
    document.querySelectorAll('.tab-content').forEach(el => el.style.display = 'none');
    // Remove 'active' class from all tab buttons
    document.querySelectorAll('.tab-button').forEach(el => el.classList.remove('active'));

    // Show the selected tab content and add 'active' class to the tab button
    document.getElementById(tabName).style.display = 'block';
    event.currentTarget.classList.add('active');
}

/**
 * Display results in the results container
 */
function displayResults(results) {
    processingResults = results;

    // Show the results container
    const resultsContainer = document.getElementById('results-container');
    if (resultsContainer) {
        resultsContainer.style.display = 'block';
    }

    // Calculate statistics
    const total = results.length;
    const successful = results.filter(r => !r.error).length;
    const failed = results.filter(r => r.error).length;

    // Update summary cards
    document.getElementById('totalFiles').textContent = total;
    document.getElementById('successCount').textContent = successful;
    document.getElementById('errorCount').textContent = failed;

    // Clear previous results
    const resultsList = document.getElementById('results');
    resultsList.innerHTML = '';

    // Add each result
    results.forEach((result, index) => {
        const resultItem = createResultItem(result);
        // Stagger the animations
        setTimeout(() => {
            resultsList.appendChild(resultItem);
        }, index * 50);
    });
}

/**
 * Create a result item element
 */
function createResultItem(result) {
    const div = document.createElement('div');
    div.className = `result-item ${result.error ? 'error' : 'success'}`;

    // Icon
    const icon = document.createElement('span');
    icon.className = 'result-icon';
    icon.textContent = result.error ? '❌' : '✅';
    div.appendChild(icon);

    // Content
    const content = document.createElement('div');
    content.className = 'result-content';

    // Filename
    const filename = document.createElement('div');
    filename.className = 'result-filename';

    if (result.renamed && result.newName) {
        filename.innerHTML = `${result.originalName} → <span style="color: var(--success-color)">${result.newName}</span>`;
    } else {
        filename.textContent = result.originalName || 'Unknown file';
    }
    content.appendChild(filename);

    // Details
    const details = document.createElement('div');
    details.className = 'result-details';

    if (result.error) {
        details.textContent = `Error: ${result.error}`;
    } else {
        const detailsText = [];
        if (result.rotated) detailsText.push(`Rotated ${result.rotationAngle || 90}°`);
        if (result.renamed) detailsText.push('Renamed');
        if (result.pageCount) detailsText.push(`${result.pageCount} pages`);
        details.textContent = detailsText.join(' • ') || 'Processed successfully';
    }
    content.appendChild(details);

    div.appendChild(content);

    return div;
}

/**
 * Download all successfully processed results
 */
function downloadAllResults() {
    const successfulResults = processingResults.filter(r => !r.error && r.url);

    if (successfulResults.length === 0) {
        showNotification('No files available for download', 'warning');
        return;
    }

    // Download each file with a small delay to avoid overwhelming the browser
    successfulResults.forEach((result, index) => {
        setTimeout(() => {
            downloadFile(result);
        }, index * 100);
    });

    showNotification(`Downloading ${successfulResults.length} files...`, 'success');
}

/**
 * Download a single file
 */
function downloadFile(result) {
    if (result.url) {
        const link = document.createElement('a');
        link.href = result.url;
        link.download = result.newName || result.originalName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}

/**
 * Reset the Rename and Rotate form
 */
function resetRenameRotateForm() {
    document.getElementById('pdf-location').value = '';
    document.getElementById('pdf-destination').value = '';
    document.getElementById('pdf-files').value = '';
    document.getElementById('rotate-checkbox').checked = false;
    document.getElementById('rename-checkbox').checked = false;
    document.getElementById('rename-source').value = '';
    document.getElementById('cross-walk').value = '';
    document.getElementById('rotation-angle').value = '90';
    document.getElementById('rename-details').style.display = 'none';
    document.getElementById('rotate-options').style.display = 'none';

    // Clear directory handles
    sourceDirectoryHandle = null;
    destinationDirectoryHandle = null;

    // Clear crosswalk mapping
    crosswalkMapping = null;

    showNotification('Form reset', 'info');
}

/**
 * Reset the Check Marketing Names form
 */
function resetCheckForm() {
    document.getElementById('marketing-file').value = '';
    document.getElementById('url-list').value = '';
    document.getElementById('prodUrl').checked = true;

    // Clear file upload input
    const fileUpload = document.getElementById('marketing-file-upload');
    if (fileUpload) {
        fileUpload.value = '';
    }

    // Clear selected marketing file
    selectedMarketingFile = null;

    showNotification('Form cleared', 'info');
}

/**
 * Show a notification message
 */
function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        font-weight: 500;
        z-index: 10000;
        animation: slideInRight 0.3s ease;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    `;

    // Set background color based on type
    const colors = {
        success: '#28a745',
        error: '#dc3545',
        warning: '#ffc107',
        info: '#667eea'
    };
    notification.style.backgroundColor = colors[type] || colors.info;
    notification.textContent = message;

    document.body.appendChild(notification);

    // Remove after 3 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

/**
 * Show loading overlay
 */
function showLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = 'flex';
    }
}

/**
 * Hide loading overlay
 */
function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = 'none';
    }
}

/**
 * Initialize event listeners
 */
function initializeEventListeners() {
    // Rename checkbox listener
    const renameCheckbox = document.getElementById('rename-checkbox');
    if (renameCheckbox) {
        renameCheckbox.addEventListener('change', function() {
            const renameDetails = document.getElementById('rename-details');
            if (renameDetails) {
                renameDetails.style.display = this.checked ? 'block' : 'none';
            }
        });
    }

    // Rotate checkbox listener
    const rotateCheckbox = document.getElementById('rotate-checkbox');
    if (rotateCheckbox) {
        rotateCheckbox.addEventListener('change', function() {
            const rotateOptions = document.getElementById('rotate-options');
            if (rotateOptions) {
                rotateOptions.style.display = this.checked ? 'block' : 'none';
            }
        });
    }

    // File input change listener
    const pdfFiles = document.getElementById('pdf-files');
    if (pdfFiles) {
        pdfFiles.addEventListener('change', function(e) {
            const fileCount = e.target.files.length;
            if (fileCount > 0) {
                showNotification(`${fileCount} file(s) selected`, 'info');
            }
        });
    }

    // Directory picker buttons
    const btnPickSource = document.getElementById('btn-pick-source');
    if (btnPickSource) {
        btnPickSource.addEventListener('click', handlePickSourceDirectory);
    }

    const btnPickDestination = document.getElementById('btn-pick-destination');
    if (btnPickDestination) {
        btnPickDestination.addEventListener('click', handlePickDestinationDirectory);
    }

    const btnPickDownloadDestination = document.getElementById('btn-pick-download-destination');
    if (btnPickDownloadDestination) {
        btnPickDownloadDestination.addEventListener('click', handlePickDestinationDirectory);
    }

    // Crosswalk CSV file picker button
    const btnPickCrosswalk = document.getElementById('btn-pick-crosswalk');
    if (btnPickCrosswalk) {
        btnPickCrosswalk.addEventListener('click', handlePickCrosswalkFile);
    }

    // PDF file picker button for View Content tab
    const btnPickPdfFile = document.getElementById('btn-pick-pdf-file');
    if (btnPickPdfFile) {
        btnPickPdfFile.addEventListener('click', handlePickPDFFile);
    }

    // Marketing file picker button
    const btnPickMarketingFile = document.getElementById('btn-pick-marketing-file');
    if (btnPickMarketingFile) {
        btnPickMarketingFile.addEventListener('click', handlePickMarketingFile);
    }

    // Marketing file upload change listener
    const marketingFileUpload = document.getElementById('marketing-file-upload');
    if (marketingFileUpload) {
        marketingFileUpload.addEventListener('change', function(e) {
            if (e.target.files.length > 0) {
                // Clear the selectedMarketingFile since we're using upload instead
                selectedMarketingFile = null;

                // Update the text input to show the uploaded file name
                const marketingFileInput = document.getElementById('marketing-file');
                if (marketingFileInput) {
                    marketingFileInput.value = e.target.files[0].name;
                }

                showNotification(`File selected: ${e.target.files[0].name}`, 'info');
            }
        });
    }

    // Check Marketing Names button
    const checkMarketBtn = document.getElementById('check-market');
    if (checkMarketBtn) {
        checkMarketBtn.addEventListener('click', handleCheckMarketingNames);
    }

    // Process PDFs button
    const rrSubmitBtn = document.getElementById('rr-submit');
    if (rrSubmitBtn) {
        rrSubmitBtn.addEventListener('click', handleProcessPDFs);
    }

    // View PDF Content button
    const viewPdfBtn = document.getElementById('view-pdf');
    if (viewPdfBtn) {
        viewPdfBtn.addEventListener('click', handleViewPDFContent);
    }

    // View PDF Fields button
    const viewFieldsBtn = document.getElementById('view-fields');
    if (viewFieldsBtn) {
        viewFieldsBtn.addEventListener('click', handleViewPDFFields);
    }

    // PDF file upload change listener
    const pdfFileUpload = document.getElementById('pdf-file-upload');
    if (pdfFileUpload) {
        pdfFileUpload.addEventListener('change', function(e) {
            if (e.target.files.length > 0) {
                // Clear the selectedPDFFile since we're using upload instead
                selectedPDFFile = null;

                // Update the text input to show the uploaded file name
                const pdfFileInput = document.getElementById('pdf-file');
                if (pdfFileInput) {
                    pdfFileInput.value = e.target.files[0].name;
                }

                showNotification(`File selected: ${e.target.files[0].name}`, 'info');
            }
        });
    }
}

/**
 * Handle picking source directory
 */
async function handlePickSourceDirectory() {
    if (!isFileSystemAccessSupported()) {
        showNotification('File System Access API not supported in this browser', 'error');
        return;
    }

    try {
        sourceDirectoryHandle = await pickDirectory('read');
        if (sourceDirectoryHandle) {
            document.getElementById('pdf-location').value = sourceDirectoryHandle.name;
            showNotification(`Source directory selected: ${sourceDirectoryHandle.name}`, 'success');
        }
    } catch (error) {
        console.error('Error picking source directory:', error);
        showNotification('Failed to select source directory', 'error');
    }
}

/**
 * Handle picking destination directory
 */
async function handlePickDestinationDirectory() {
    if (!isFileSystemAccessSupported()) {
        showNotification('File System Access API not supported in this browser', 'error');
        return;
    }

    try {
        destinationDirectoryHandle = await pickDirectory('readwrite');
        if (destinationDirectoryHandle) {
            document.getElementById('pdf-destination').value = destinationDirectoryHandle.name;
            document.getElementById('download-destination').value = destinationDirectoryHandle.name;
            showNotification(`Destination directory selected: ${destinationDirectoryHandle.name}`, 'success');
        }
    } catch (error) {
        console.error('Error picking destination directory:', error);
        showNotification('Failed to select destination directory', 'error');
    }
}

/**
 * Handle picking marketing file (Excel or CSV)
 */
async function handlePickMarketingFile() {
    if (!('showOpenFilePicker' in window)) {
        showNotification('File System Access API not supported. Please use the file upload input below.', 'warning');
        return;
    }

    try {
        const [fileHandle] = await window.showOpenFilePicker({
            types: [{
                description: 'Excel or CSV Files',
                accept: {
                    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': ['.xlsx'],
                    'application/vnd.ms-excel': ['.xls'],
                    'text/csv': ['.csv']
                }
            }],
            multiple: false
        });

        if (fileHandle) {
            selectedMarketingFile = await fileHandle.getFile();
            document.getElementById('marketing-file').value = selectedMarketingFile.name;

            // Clear the file upload input if it had something
            const fileUpload = document.getElementById('marketing-file-upload');
            if (fileUpload) {
                fileUpload.value = '';
            }

            showNotification(`Marketing file selected: ${selectedMarketingFile.name}`, 'success');
        }
    } catch (error) {
        if (error.name !== 'AbortError') {
            console.error('Error picking marketing file:', error);
            showNotification('Failed to select marketing file', 'error');
        }
    }
}

/**
 * Handle picking a single PDF file for viewing
 */
async function handlePickPDFFile() {
    if (!('showOpenFilePicker' in window)) {
        showNotification('File System Access API not supported. Please use the file upload input below.', 'warning');
        return;
    }

    try {
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
            selectedPDFFile = await fileHandle.getFile();
            document.getElementById('pdf-file').value = selectedPDFFile.name;

            // Clear the file upload input if it had something
            const fileUpload = document.getElementById('pdf-file-upload');
            if (fileUpload) {
                fileUpload.value = '';
            }

            showNotification(`PDF file selected: ${selectedPDFFile.name}`, 'success');
        }
    } catch (error) {
        if (error.name !== 'AbortError') {
            console.error('Error picking PDF file:', error);
            showNotification('Failed to select PDF file', 'error');
        }
    }
}

/**
 * Handle picking crosswalk CSV file
 */
async function handlePickCrosswalkFile() {
    try {
        // Create file input for CSV selection
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = '.csv,.txt';

        fileInput.onchange = async function(e) {
            const file = e.target.files[0];
            if (file) {
                try {
                    showLoading();

                    // Read and parse the CSV file
                    crosswalkMapping = await readCSVFile(file);

                    // Update the UI
                    document.getElementById('rename-source').value = file.name;

                    // Clear the manual textarea since we're using CSV
                    document.getElementById('cross-walk').value = '';

                    hideLoading();

                    // Show success with count
                    const mappingCount = Object.keys(crosswalkMapping).length;
                    showNotification(`Loaded ${mappingCount} rename mapping(s) from ${file.name}`, 'success');

                } catch (error) {
                    hideLoading();
                    console.error('Error reading CSV file:', error);
                    showNotification(`Error reading CSV: ${error.message}`, 'error');
                    crosswalkMapping = null;
                }
            }
        };

        fileInput.click();
    } catch (error) {
        console.error('Error picking crosswalk file:', error);
        showNotification('Failed to select crosswalk file', 'error');
    }
}

/**
 * Handle processing PDFs with directory or file input
 */
async function handleProcessPDFs() {
    try {
        showLoading();

        // Get processing options
        const shouldRotate = document.getElementById('rotate-checkbox').checked;
        const shouldRename = document.getElementById('rename-checkbox').checked;
        const rotationAngle = parseInt(document.getElementById('rotation-angle').value);
        const crosswalkText = document.getElementById('cross-walk').value;

        // Determine which crosswalk mapping to use: CSV or manual text
        let renameMapping = null;
        if (shouldRename) {
            if (crosswalkMapping && Object.keys(crosswalkMapping).length > 0) {
                // Use CSV mapping
                renameMapping = crosswalkMapping;
            } else if (crosswalkText.trim()) {
                // Parse manual text mapping
                renameMapping = parseCrosswalk(crosswalkText);
            }
        }

        // Check if using directory or file input
        const fileInput = document.getElementById('pdf-files');
        let files = [];

        if (sourceDirectoryHandle && destinationDirectoryHandle) {
            // Use directory handles
            files = await getFilesFromDirectory(sourceDirectoryHandle);

            if (files.length === 0) {
                hideLoading();
                showNotification('No PDF files found in source directory', 'warning');
                return;
            }

            showNotification(`Found ${files.length} PDF files. Processing...`, 'info');

            // Process files
            const options = {
                rotate: shouldRotate,
                rename: shouldRename,
                rotationAngle: rotationAngle,
                renameMap: renameMapping || {}
            };

            const results = await processRenameAndRotate(files, options);

            // Save to destination directory
            for (const result of results) {
                if (!result.error && result.blob) {
                    const saveResult = await saveToDirectory(
                        destinationDirectoryHandle,
                        result.newName || result.originalName,
                        result.blob
                    );

                    if (!saveResult.success) {
                        result.error = saveResult.error;
                        result.saved = false;
                    } else {
                        result.saved = true;
                    }
                }
            }

            hideLoading();
            displayResults(results);
            showNotification(`Processing complete! ${results.filter(r => !r.error).length} files saved to destination.`, 'success');

        } else if (fileInput.files.length > 0) {
            // Use file input (download mode)
            files = Array.from(fileInput.files);

            showNotification(`Processing ${files.length} files...`, 'info');

            const options = {
                rotate: shouldRotate,
                rename: shouldRename,
                rotationAngle: rotationAngle,
                renameMap: renameMapping || {}
            };

            const results = await processRenameAndRotate(files, options);

            hideLoading();
            displayResults(results);

            // Ask if user wants to download
            if (results.filter(r => !r.error).length > 0) {
                if (confirm('Download processed PDFs?')) {
                    downloadProcessedPDFs(results);
                }
            }
        } else {
            hideLoading();
            showNotification('Please select a source directory or files to process', 'warning');
        }

    } catch (error) {
        console.error('Error processing PDFs:', error);
        hideLoading();
        showNotification(`Error: ${error.message}`, 'error');
    }
}

/**
 * Handle checking marketing names
 */
async function handleCheckMarketingNames() {
    try {
        showLoading();

        const region = document.querySelector('input[name="region"]:checked')?.value || 'prod';
        let urls = [];

        // Try to get URLs from file
        const marketingFile = await getMarketingFileFromInput();
        if (marketingFile) {
            try {
                urls = await readMarketingFile(marketingFile, region);
                showNotification(`Extracted ${urls.length} entries from ${marketingFile.name}`, 'success');
            } catch (error) {
                hideLoading();
                showNotification(`Error reading file: ${error.message}`, 'error');
                return;
            }
        } else {
            // Try to get URLs from textarea
            const urlListText = document.getElementById('url-list').value;
            if (urlListText && urlListText.trim()) {
                urls = urlListText.split('\n')
                    .map(url => url.trim())
                    .filter(url => url.length > 0);
                showNotification(`Processing ${urls.length} URLs from manual input`, 'info');
            }
        }

        // Check if we have any URLs to process
        if (urls.length === 0) {
            hideLoading();
            showNotification('Please select a file or enter URLs to check', 'warning');
            return;
        }

        // Display the extracted URLs
        displayMarketingNames(urls, region);
        hideLoading();

        showNotification(`Ready to check ${urls.length} marketing name(s)`, 'info');

    } catch (error) {
        console.error('Error checking marketing names:', error);
        hideLoading();
        showNotification(`Error: ${error.message}`, 'error');
    }
}

/**
 * Handle viewing PDF content
 */
async function handleViewPDFContent() {
    try {
        const pdfFile = await getPDFFileFromInput();
        if (!pdfFile) {
            showNotification('Please select a PDF file first', 'warning');
            return;
        }

        showLoading();

        // Read and extract PDF content
        const arrayBuffer = await pdfFile.arrayBuffer();
        const pdfDoc = await PDFDocument.load(arrayBuffer);

        const pageCount = pdfDoc.getPageCount();
        const metadata = {
            title: pdfDoc.getTitle() || 'N/A',
            author: pdfDoc.getAuthor() || 'N/A',
            subject: pdfDoc.getSubject() || 'N/A',
            creator: pdfDoc.getCreator() || 'N/A',
            producer: pdfDoc.getProducer() || 'N/A',
            keywords: pdfDoc.getKeywords() || 'N/A'
        };

        hideLoading();

        // Display results
        displayPDFContent(pdfFile.name, pageCount, metadata);
        showNotification('PDF content extracted successfully', 'success');

    } catch (error) {
        console.error('Error viewing PDF content:', error);
        hideLoading();
        showNotification(`Error: ${error.message}`, 'error');
    }
}

/**
 * Handle viewing PDF form fields
 */
async function handleViewPDFFields() {
    try {
        const pdfFile = await getPDFFileFromInput();
        if (!pdfFile) {
            showNotification('Please select a PDF file first', 'warning');
            return;
        }

        showLoading();

        // Read and extract PDF form fields
        const arrayBuffer = await pdfFile.arrayBuffer();
        const pdfDoc = await PDFDocument.load(arrayBuffer);

        const form = pdfDoc.getForm();
        const fields = form.getFields();

        const fieldData = fields.map(field => {
            const fieldName = field.getName();
            let fieldType = 'Unknown';
            let fieldValue = 'N/A';

            try {
                // Determine field type
                if (field.constructor.name.includes('Text')) {
                    fieldType = 'Text Field';
                    fieldValue = field.getText() || '(empty)';
                } else if (field.constructor.name.includes('CheckBox')) {
                    fieldType = 'Checkbox';
                    fieldValue = field.isChecked() ? 'Checked' : 'Unchecked';
                } else if (field.constructor.name.includes('Radio')) {
                    fieldType = 'Radio Button';
                    fieldValue = field.getSelected() || '(none selected)';
                } else if (field.constructor.name.includes('Dropdown')) {
                    fieldType = 'Dropdown';
                    fieldValue = field.getSelected() || '(none selected)';
                } else if (field.constructor.name.includes('Option')) {
                    fieldType = 'Option List';
                    fieldValue = field.getSelected().join(', ') || '(none selected)';
                } else {
                    fieldType = field.constructor.name;
                }
            } catch (e) {
                // If we can't read the value, just note it
                fieldValue = '(unable to read)';
            }

            return {
                name: fieldName,
                type: fieldType,
                value: fieldValue
            };
        });

        hideLoading();

        // Display results
        displayPDFFields(pdfFile.name, fieldData);

        if (fieldData.length === 0) {
            showNotification('No form fields found in this PDF', 'info');
        } else {
            showNotification(`Found ${fieldData.length} form field(s)`, 'success');
        }

    } catch (error) {
        console.error('Error viewing PDF fields:', error);
        hideLoading();
        showNotification(`Error: ${error.message}`, 'error');
    }
}

/**
 * Get PDF file from either upload input, selected file, or file path
 */
async function getPDFFileFromInput() {
    // First, try the file upload input
    const fileUpload = document.getElementById('pdf-file-upload');
    if (fileUpload && fileUpload.files.length > 0) {
        return fileUpload.files[0];
    }

    // Second, try the selected PDF file from File System Access API
    if (selectedPDFFile) {
        return selectedPDFFile;
    }

    return null;
}

/**
 * Display PDF content information
 */
function displayPDFContent(filename, pageCount, metadata) {
    const resultsContainer = document.getElementById('results-container');
    const resultsList = document.getElementById('results');

    if (!resultsContainer || !resultsList) return;

    // Show the results container
    resultsContainer.style.display = 'block';

    // Update summary
    document.getElementById('totalFiles').textContent = '1';
    document.getElementById('successCount').textContent = '1';
    document.getElementById('errorCount').textContent = '0';

    // Build content HTML
    let html = '<div style="padding: 20px;">';
    html += `<h3 style="margin-top: 0; color: var(--primary-color);">📄 ${filename}</h3>`;

    html += '<div style="background: var(--bg-secondary); padding: 15px; border-radius: 8px; margin-bottom: 15px;">';
    html += `<div style="margin-bottom: 10px;"><strong>Page Count:</strong> ${pageCount}</div>`;
    html += '</div>';

    html += '<h4 style="color: var(--text-primary); margin-bottom: 10px;">Document Metadata</h4>';
    html += '<div style="background: var(--bg-secondary); padding: 15px; border-radius: 8px;">';

    for (const [key, value] of Object.entries(metadata)) {
        html += `<div style="margin-bottom: 8px; display: flex; border-bottom: 1px solid var(--border-color); padding-bottom: 5px;">`;
        html += `<strong style="width: 120px; text-transform: capitalize;">${key}:</strong>`;
        html += `<span style="flex: 1; color: var(--text-secondary);">${value}</span>`;
        html += `</div>`;
    }

    html += '</div>';
    html += '</div>';

    resultsList.innerHTML = html;
}

/**
 * Display PDF form fields
 */
function displayPDFFields(filename, fields) {
    const resultsContainer = document.getElementById('results-container');
    const resultsList = document.getElementById('results');

    if (!resultsContainer || !resultsList) return;

    // Show the results container
    resultsContainer.style.display = 'block';

    // Update summary
    document.getElementById('totalFiles').textContent = '1';
    document.getElementById('successCount').textContent = fields.length.toString();
    document.getElementById('errorCount').textContent = '0';

    // Build fields HTML
    let html = '<div style="padding: 20px;">';
    html += `<h3 style="margin-top: 0; color: var(--primary-color);">📝 Form Fields in ${filename}</h3>`;

    if (fields.length === 0) {
        html += '<div style="text-align: center; padding: 40px; color: var(--text-secondary);">';
        html += '<p>No form fields found in this PDF document.</p>';
        html += '</div>';
    } else {
        html += `<div style="margin-bottom: 15px; color: var(--text-secondary);">Found ${fields.length} field(s)</div>`;

        fields.forEach((field, index) => {
            html += '<div style="background: var(--bg-secondary); padding: 15px; border-radius: 8px; margin-bottom: 10px; border-left: 4px solid var(--primary-color);">';
            html += `<div style="font-weight: bold; color: var(--primary-color); margin-bottom: 8px;">Field ${index + 1}</div>`;
            html += `<div style="margin-bottom: 5px;"><strong>Name:</strong> <code style="background: var(--bg-primary); padding: 2px 6px; border-radius: 3px;">${field.name}</code></div>`;
            html += `<div style="margin-bottom: 5px;"><strong>Type:</strong> ${field.type}</div>`;
            html += `<div><strong>Value:</strong> ${field.value}</div>`;
            html += '</div>';
        });
    }

    html += '</div>';

    resultsList.innerHTML = html;
}

/**
 * Get marketing file from either upload input or selected file
 */
async function getMarketingFileFromInput() {
    // First, try the file upload input
    const fileUpload = document.getElementById('marketing-file-upload');
    if (fileUpload && fileUpload.files.length > 0) {
        return fileUpload.files[0];
    }

    // Second, try the selected marketing file from File System Access API
    if (selectedMarketingFile) {
        return selectedMarketingFile;
    }

    return null;
}

/**
 * Read and parse Excel file using SheetJS
 * @param {File} file - Excel file to read
 * @returns {Promise<Array>} Array of data rows
 */
async function readExcelFile(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();

        reader.onload = function(e) {
            try {
                const data = new Uint8Array(e.target.result);
                const workbook = XLSX.read(data, { type: 'array' });

                // Get the first sheet
                const firstSheetName = workbook.SheetNames[0];
                const worksheet = workbook.Sheets[firstSheetName];

                // Convert to JSON (array of objects)
                const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

                resolve(jsonData);
            } catch (error) {
                reject(error);
            }
        };

        reader.onerror = function() {
            reject(new Error('Failed to read Excel file'));
        };

        reader.readAsArrayBuffer(file);
    });
}

/**
 * Read and parse CSV file
 * @param {File} file - CSV file to read
 * @returns {Promise<Array>} Array of data rows
 */
async function readCSVFileAsArray(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();

        reader.onload = function(e) {
            try {
                const content = e.target.result;
                const lines = content.split('\n');
                const data = lines.map(line => {
                    // Simple CSV parsing (handles basic cases)
                    return line.split(',').map(cell => cell.trim().replace(/^["']|["']$/g, ''));
                }).filter(row => row.some(cell => cell.length > 0)); // Remove empty rows

                resolve(data);
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
 * Extract URLs from Excel/CSV data
 * Looks for URLs in the data or constructs them from marketing names
 * @param {Array} data - Array of data rows
 * @param {string} region - Region code ('prod' or 'ps')
 * @returns {Array} Array of URLs or marketing data objects
 */
function extractURLsFromData(data, region = 'prod') {
    const urls = [];

    // Skip header row if it exists
    let startIndex = 0;
    if (data.length > 0) {
        const firstRow = data[0];
        // Check if first row looks like a header
        const isHeader = firstRow.some(cell =>
            typeof cell === 'string' &&
            (cell.toLowerCase().includes('url') ||
             cell.toLowerCase().includes('name') ||
             cell.toLowerCase().includes('marketing'))
        );
        if (isHeader) startIndex = 1;
    }

    // Process each row
    for (let i = startIndex; i < data.length; i++) {
        const row = data[i];

        // Skip empty rows
        if (!row || row.every(cell => !cell)) continue;

        // Try to find a URL in the row
        let url = null;
        for (const cell of row) {
            if (typeof cell === 'string' && (cell.startsWith('http://') || cell.startsWith('https://'))) {
                url = cell;
                break;
            }
        }

        // If URL found, add it
        if (url) {
            urls.push(url);
        } else {
            // If no URL found, treat the first non-empty cell as marketing name or URL
            const firstCell = row.find(cell => cell && cell.toString().trim());
            if (firstCell) {
                urls.push(firstCell.toString().trim());
            }
        }
    }

    return urls;
}

/**
 * Extract URLs from Excel/CSV data
 * Looks for URLs in the data or constructs them from marketing names
 * @param {Array} data - Array of data rows
 * @param {string} region - Region code ('prod' or 'ps')
 * @returns {Array} Array of marketing data objects
 */
function extractMarketingDataFromData(data, region = 'prod') {
    const marketingData = []; // {marketingName: <name>, url: <url>}[]

    // Skip header row if it exists
    let startIndex = 0;
    let firstRow;
    let isHeader;
    if (data.length > 0) {
        firstRow = data[0];
        // Check if first row looks like a header
        isHeader = firstRow.some(cell =>
            typeof cell === 'string' &&
            (cell.toLowerCase().includes('url') ||
                cell.toLowerCase().includes('name') ||
                cell.toLowerCase().includes('marketing'))
        );
        if (isHeader) startIndex = 1;
    }

    // Process each row
    for (let i = startIndex; i < data.length; i++) {
        const row = data[i];

        // Skip empty rows
        if (!row || row.every(cell => !cell)) continue;

        let outputRow = {};
        for (const index in row) {
            outputRow[firstRow[index]] = row[index];
        }
        marketingData.push(outputRow);
    }

    return marketingData;
}

/**
 * Read marketing file and extract URLs
 * @param {File} file - Marketing file (Excel or CSV)
 * @param {string} region - Region code
 * @returns {Promise<Array>} Array of URLs
 */
async function readMarketingFile(file, region = 'prod') {
    const fileName = file.name.toLowerCase();
    let data;

    if (fileName.endsWith('.xlsx') || fileName.endsWith('.xls')) {
        // Read Excel file
        data = await readExcelFile(file);
    } else if (fileName.endsWith('.csv')) {
        // Read CSV file
        data = await readCSVFileAsArray(file);
    } else {
        throw new Error('Unsupported file format. Please use .xlsx, .xls, or .csv files.');
    }

    // Extract URLs from the data
    return extractURLsFromData(data, region);
}

/**
 * Display marketing names/URLs in the results container
 * @param {Array} urls - Array of URLs or marketing names
 * @param {string} region - Region code
 */
function displayMarketingNames(urls, region) {
    const resultsContainer = document.getElementById('results-container');
    const resultsList = document.getElementById('results');

    if (!resultsContainer || !resultsList) return;

    // Show the results container
    resultsContainer.style.display = 'block';

    // Update summary
    document.getElementById('totalFiles').textContent = urls.length.toString();
    document.getElementById('successCount').textContent = urls.length.toString();
    document.getElementById('errorCount').textContent = '0';

    // Build results HTML
    let html = '<div style="padding: 20px;">';
    html += `<h3 style="margin-top: 0; color: var(--primary-color);">Marketing Names / URLs</h3>`;
    html += `<div style="margin-bottom: 15px; color: var(--text-secondary);">Environment: <strong>${region === 'prod' ? 'Production' : 'Production Stage'}</strong></div>`;
    html += `<div style="margin-bottom: 15px; color: var(--text-secondary);">Total entries: <strong>${urls.length}</strong></div>`;

    html += '<div style="background: var(--bg-secondary); padding: 15px; border-radius: 8px; max-height: 500px; overflow-y: auto;">';

    urls.forEach((url, index) => {
        let body =
        html += '<div style="padding: 10px; margin-bottom: 8px; background: var(--bg-primary); border-radius: 4px; border-left: 3px solid var(--primary-color);">';
        html += `<div style="font-weight: bold; color: var(--primary-color); margin-bottom: 5px;">Entry ${index + 1}</div>`;
        html += `<div style="word-break: break-all;">${url}</div>`;
        html += `<div style="word-break: break-all;">${url}</div>`;
        html += `<div style="word-break: break-all;">${url}</div>`;
        html += '</div>';
    });

    html += '</div>';

    html += '<div style="margin-top: 20px; padding: 15px; background: var(--bg-secondary); border-radius: 8px; border-left: 4px solid #ffc107;">';
    html += '<strong>Note:</strong> The backend API for validating marketing names is not currently connected. ';
    html += 'The URLs/names have been successfully extracted from your file. ';
    html += 'To enable validation, you would need to implement or connect to a marketing name validation service.';
    html += '</div>';

    html += '</div>';

    resultsList.innerHTML = html;
}

// Add CSS animation keyframes if not already in CSS
if (!document.querySelector('#notification-animations')) {
    const style = document.createElement('style');
    style.id = 'notification-animations';
    style.textContent = `
        @keyframes slideInRight {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        @keyframes slideOutRight {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
    `;
    document.head.appendChild(style);
}

/*/!* File: scripts/check-marketing.js *!/
/!* Requires pdf.js and xlsx already included in index.html (they are in the page). *!/
(function () {
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

    pickMappingBtn.addEventListener('click', () => mappingFileEl.click());
    mappingFileEl.addEventListener('change', (e) => {
        const f = e.target.files[0];
        mappingDisplayEl.value = f ? f.name : '';
    });

    clearBtn.addEventListener('click', () => {
        urlListEl.value = '';
        mappingFileEl.value = '';
        mappingDisplayEl.value = '';
    });

    closeResultsBtn.addEventListener('click', () => { resultsEl.style.display = 'none'; });
    clearResultsBtn.addEventListener('click', () => {
        resultsListEl.innerHTML = '';
        totalEl.textContent = '0';
        matchesEl.textContent = '0';
        failsEl.textContent = '0';
    });

    runBtn.addEventListener('click', async () => {
        runBtn.disabled = true;
        resultsListEl.innerHTML = '';
        resultsEl.style.display = 'block';
        totalEl.textContent = '0';
        matchesEl.textContent = '0';
        failsEl.textContent = '0';

        let mappings = await loadMappingsFromFile(mappingFileEl.files[0]);
        let urls = extractUrls(urlListEl.value);

        // If mappings provided, use their URLs as canonical list
        if (mappings.length) {
            urls = mappings.map(m => m.url);
            // Build map of expected names
        }

        const total = urls.length;
        totalEl.textContent = String(total);

        let matchCount = 0;
        let failCount = 0;
        for (let i = 0; i < urls.length; i++) {
            const url = urls[i];
            const mapping = mappings.find(m => normalizeUrl(m.url) === normalizeUrl(url)) || {};
            const expected = mapping.expectedName || mapping.name || extractNameFromUrl(url) || null;
            appendResultRow(url, 'Processing...', 'pending');
            try {
                const arrayBuffer = await fetchPdfArrayBuffer(url);
                // If download requested, trigger save
                if (downloadCheckbox.checked) triggerDownload(arrayBuffer, filenameFromUrl(url));
                const extractedText = await extractTextFromPdfBuffer(arrayBuffer);
                const found = findMarketingName(extractedText, searchModeEl.value);
                const match = compareNames(found, expected);
                updateResultRow(url, match ? 'Match' : 'Mismatch', match ? 'success' : 'error', { expected, found });
                if (match) matchCount++; else failCount++;
            } catch (err) {
                updateResultRow(url, `Error: ${err.message}`, 'error');
                failCount++;
            }
            matchesEl.textContent = String(matchCount);
            failsEl.textContent = String(failCount);
            await delay(200); // small pause to keep UI responsive
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
        const text = await readFileAsArrayBuffer(file);
        const name = (file.name || '').toLowerCase();
        if (name.endsWith('.csv')) {
            const csv = new TextDecoder('utf-8').decode(text);
            return parseCsvMappings(csv);
        }
        // try XLS/XLSX
        try {
            const wb = XLSX.read(text, { type: 'array' });
            const sheetName = wb.SheetNames[0];
            const json = XLSX.utils.sheet_to_json(wb.Sheets[sheetName], { defval: '' });
            // normalize keys: url and expectedName/name
            return json.map(row => {
                const url = row.url || row.URL || row.link || row.Link || '';
                const expectedName = row.expectedName || row.expected_name || row.name || row.Name || '';
                return { url: String(url).trim(), expectedName: expectedName ? String(expectedName).trim() : '' };
            }).filter(r => r.url);
        } catch (e) {
            return [];
        }
    }

    function parseCsvMappings(csv) {
        const lines = csv.split(/\r?\n/).filter(Boolean);
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

    async function fetchPdfArrayBuffer(url) {
        const resp = await fetch(url);
        if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
        return await resp.arrayBuffer();
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

    function triggerDownload(arrayBuffer, filename) {
        const blob = new Blob([arrayBuffer], { type: 'application/pdf' });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = filename.endsWith('.pdf') ? filename : filename + '.pdf';
        document.body.appendChild(a);
        a.click();
        a.remove();
        setTimeout(() => URL.revokeObjectURL(a.href), 5000);
    }

    async function extractTextFromPdfBuffer(arrayBuffer) {
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
            // look for "Marketing Name" label and capture following text on the same line
            const re = /Marketing\s*Name[:\-\s]*([^\n\r]+)/i;
            const m = text.match(re);
            if (m && m[1]) return m[1].trim();
        }
        // fallback: try to find lines with 'name' keyword or return empty
        const lines = text.split(/\r?\n/).map(l => l.trim()).filter(Boolean);
        for (const line of lines) {
            if (/marketing.*name/i.test(line)) {
                const sub = line.replace(/.*marketing.*name[:\-\s]*!/i, '').trim();
                if (sub) return sub;
            }
        }
        return ''; // nothing found
    }

    function compareNames(found, expected) {
        if (!expected && !found) return false;
        if (!expected && found) return true; // found something and no expected to compare
        if (!found && expected) return false;
        // simple case-insensitive contains check
        return String(found).toLowerCase().includes(String(expected).toLowerCase()) ||
            String(expected).toLowerCase().includes(String(found).toLowerCase());
    }

    function extractNameFromUrl(url) {
        try {
            const u = new URL(url);
            // check query param 'name'
            const nameParam = u.searchParams.get('name') || u.searchParams.get('marketing') || u.searchParams.get('title');
            if (nameParam) return decodeURIComponent(nameParam);
            // fallback to last path segment without extension
            const seg = u.pathname.split('/').filter(Boolean).pop() || '';
            return seg.replace(/\.pdf$/i, '').replace(/[-_]/g, ' ');
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
})();*/


// Export functions for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        clearResults,
        closeResults,
        displayResults,
        downloadAllResults,
        resetRenameRotateForm,
        resetCheckForm,
        showNotification,
        showLoading,
        hideLoading,
        openTab,
        extractMarketingDataFromData
    };
}