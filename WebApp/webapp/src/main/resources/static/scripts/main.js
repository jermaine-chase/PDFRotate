// Set "Home" tab as the default active tab with welcome message
$(document).ready(function() {
    // Hide all tab contents
    $('.tab-content').hide();

    $('#rename-details').hide();

    const defaultTab = $('#home-tab');
    defaultTab.show();

    const defaultButton = $('.tab-button:first');
    defaultButton.addClass('active');
});

let openTab = function(event, tabName) {
    // Hide all tab contents
    $('.tab-content').hide();
    // Remove 'active' class from all tab buttons
    $('.tab-button').removeClass('active');

    // Show the selected tab content and add 'active' class to the tab button
    $('#' + tabName).show();
    $(event.currentTarget).addClass('active');
}

$('#rr-submit').on('click', function() {
    $('#results').html("");
    let req = {
        'source': $('#pdf-location').val(),
        'destination': $('#pdf-destination').val(),
        'rotate': $('#rotate-checkbox').prop('checked'),
        'rename': $('#rename-checkbox').prop('checked')
    };

    if (req.rename) {
        req['rename-source'] = $('#rename-source').val();
        req['rename-list'] = $('#cross-walk').val();
    }

    $.post('/renameAndRotate', JSON.stringify(req), function(data) {
        console.log(data);
        $('#results').html(data);
    });
})

$('#view-pdf').on('click', function() {
    $('#results').html("");
    let req = {
        'file': $('#pdf-file').val()
    };

    $.post('/getPdfContent', req, function(data) {
        console.log(data);
        $('#results').html(data);
    });
})

$('#view-fields').on('click', function() {
    $('#results').html("");
    let req = {
        'file': $('#pdf-file').val()
    };

    $.post('/getPdfFields', req, function(data) {
        console.log(data);
        $('#results').html(data);
    });
})

$('#check-market').on('click', async function() {
    $('#results').html("");
    const urlList = $('#url-list').val()
    if (urlList && urlList.trim().length > 0) {
        const urls = urlList.split('\n');
        try {
            const batchSize = 10;
            const numBatches = Math.ceil(urls.length / batchSize);

            for (let i = 0; i < numBatches; i++) {
                const batchUrls = urls.slice(i * batchSize, (i + 1) * batchSize);
                const promises = batchUrls.map(url =>
                    $.ajax({
                    url: '/compareMarketName',
                    type: 'POST',
                    data: JSON.stringify({'Production URL': url}),
                    contentType: "application/json; charset=utf-8"
                    })
                );

                const responses = await Promise.all(promises);

                responses.forEach(r => {
                    console.log(r);
                    $('#results').append(r + '<br/>');
                })

            }
        } catch (error) {
            console.error(error);
        }
    } else if ($('#marketing-file').val() && $('#marketing-file').val().trim().length > 0) {
        $.ajax({
            url: '/readExcel',
            type: 'POST',
            data: JSON.stringify({
                fileName: $('#marketing-file').val(),
                region: $('input[name="region"]:checked').val()
            }),
            contentType: "application/json; charset=utf-8",
            success: async function (data) {
                console.log(data);
                if (data && data.trim().length > 0) {
                    const fileData = JSON.parse(data);
                    try {
                        const batchSize = 10;
                        const numBatches = Math.ceil(fileData.length / batchSize);

                        for (let i = 0; i < numBatches; i++) {
                            const batchUrls = fileData.slice(i * batchSize, (i + 1) * batchSize);
                            const promises = batchUrls.map(url => $.ajax({
                                    url: '/compareMarketName',
                                    type: 'POST',
                                    data: JSON.stringify(url),
                                    contentType: "application/json; charset=utf-8"
                                })
                            );

                            const responses = await Promise.all(promises);

                            responses.forEach(r => {
                                console.log(r);
                                $('#results').append(r + '<br/>');
                            })

                        }
                    } catch (error) {
                        console.error(error);
                    }
                }
            }
        });
    }
})

$('#rename-checkbox').on('click', function () {
    if($('#rename-checkbox').prop('checked')) {
        $('#rename-details').show();
    } else {
        $('#rename-details').hide();
    }
})
