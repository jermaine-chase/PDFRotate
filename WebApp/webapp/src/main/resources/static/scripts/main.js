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
    let req = {
        'source': $('#pdf-location').val(),
        'destination': $('#pdf-destination').val(),
        'rotate': $('#rotate-checkbox').prop('checked'),
        'rename': $('#rename-checkbox').prop('checked')
    };

    $.post('/tools/renameAndRotate', req, function(data) {
        console.log(data);
    });
})

$('#check-market').on('click', function() {
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

    $.post('/tools/renameAndRotate', req, function(data) {
        console.log(data);
    });
})

$('#rename-checkbox').on('click', function () {
    if($('#rename-checkbox').prop('checked')) {
        $('#rename-details').show();
    } else {
        $('#rename-details').hide();
    }

})