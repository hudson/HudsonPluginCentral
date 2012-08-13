
jQuery.noConflict();

var selected;
var filesToUpload;

jQuery(document).ready(function() {
        
    jQuery("#progressbar").progressbar({
        value: 0
    });
    
    jQuery("#progressbar").height(5);
            
    jQuery('#fileSelect').change(function(e){
        filesToUpload = e.target.files;
    });

    jQuery('#updateButton').button();
    jQuery('#updateButton').click(function() {
        submitUpdateForm();
    });
    
    jQuery('#deleteButton').button();
    jQuery('#deleteButton').click(function() {
        jQuery('#dialog-confirm').dialog({
            resizable: false,
            height:185,
            width: 350,
            modal: true,
            buttons: {
                'Delete': function() {
                    jQuery( this ).dialog( "close" );
                    submitDeleteForm();
                },
                Cancel: function() {
                    jQuery( this ).dialog( "close" );
                }
            }
        });
    });
    
    jQuery('#logoutLink').click(function() {
         doLogout();
    });
    
    jQuery('#uploadLink').click(function() {
        jQuery('#dialog-upload').dialog({
            resizable: false,
            height:185,
            width: 450,
            modal: true,
            buttons: {
                'Upload': function() {
                    for (var i = 0, f; f = filesToUpload[i]; i++) {
                        uploadFile(f);
                    }
                },
                Cancel: function() {
                    jQuery( this ).dialog("close");
                }
            }
        });
    });
    
    jQuery("#selectable").selectable({
        selected: function(event, ui) {
            jQuery("#pluginUpdateMsg").hide();
            setButtonsVisibility(true)
            selected = jQuery(ui.selected);
            jQuery("#pluginInfo").load('plugins/' + jQuery(selected).text());
        }    
    });
    
    // select the first item
    selectSelectableElement (jQuery("#selectable"), jQuery("#selectable").children(":eq(0)"));
    
    jQuery('#loginLink').click(function(e){
        jQuery('#loginError').hide();
        jQuery('#loginMsg').hide();
        jQuery('#loginDialog').dialog({
            resizable: false,
            height:250,
            width: 350,
            modal: true,
            buttons: {
                'Login': function() {
                    submitLoginForm();
                },
                Cancel: function() {
                    jQuery('#j_username').attr({
                        value:""
                    });
                    jQuery('#j_password').attr({
                        value:""
                    });
                    jQuery( this ).dialog("close");
                }
            }
        });
    });
    
    jQuery('#j_username').keypress(function(e){
        if(e.which == 13){
            submitLoginForm();
        }
    });
            
    jQuery('#j_password').keypress(function(e){
        if(e.which == 13){
            submitLoginForm();
        }
    });

    jQuery('#loginButton').click(function() {
        submitLoginForm();
    });
            
});

function submitLoginForm(){
    jQuery('#loginError').hide();
    jQuery('#loginMsg').show();
    var dataString = jQuery("#loginForm").serialize();
    jQuery.ajax({
        type: 'POST',
        url: "login",
        data: dataString,
        success: function(){
            window.location.href=".";
        },
        error: function(msg){
            jQuery('#loginError').show();
            jQuery('#loginError').text(msg.responseText);
            jQuery('#loginMsg').hide();
        },
        dataType: "html"
    }); 
}

function doLogout(){
     
    jQuery.ajax({
        type: 'POST',
        url: "logout",
        success: function(){
            window.location.href=".";
        },
        error: function(){
             
        }
    }); 
}

function showLoginDialog(){
    jQuery.blockUI({
        message: jQuery('#loginDialog'),
        css: { 
            width: '350px'
        },
        title:  'Confirmation'
    });
    jQuery('j_username').focus();
}

function submitDeleteForm(){
    jQuery("#pluginUpdateMsg").hide();
    setButtonsVisibility(false)
    jQuery.ajax({
        type: 'POST',
        url: "deletePlugin",
        data: {
            name:jQuery(selected).text()
        },
        success: function(){
            jQuery(selected).remove();
            jQuery("#pluginInfo").empty();
            jQuery("#pluginInfo").append('<p style="font-size: 18px;">Plugin <span style="font-size: 18px; font-weight:bold">' + jQuery(selected).text() +'</span> successfully deleted </p>');
        },
        error: function(msg){
            jQuery('#loginError').text(msg.responseText);
            jQuery("#errorMsg").show();
        }
    }); 
}

function submitUpdateForm(){
    var dataString = jQuery("#updateForm").serialize();
    jQuery.ajax({
        type: 'POST',
        url: "updatePlugin",
        data: dataString,
        success: function(){
            showMessage("Plugin updated successfully.", false, false);
        },
        error: function(msg){
            showMessage(msg.responseText, true, false);
        }
    }); 
}

function showMessage(msg, error, upload){
    var infoTxt;
    
    if (upload == true){
        infoTxt = jQuery("#pluginUploadMsg");
    }else{
        infoTxt = jQuery("#pluginUpdateMsg");
    }
    infoTxt.text(msg);
    if (error == true){
        infoTxt.css("color","red");
    }else{
        infoTxt.css("color","green");  
    }
    infoTxt.show();
}


function setButtonsVisibility(visible){
    if (visible == true){
        jQuery("#updateButton").show();
        jQuery("#deleteButton").show();
    }else{
        jQuery("#updateButton").hide();
        jQuery("#deleteButton").hide();
    }
}

function uploadFile(file) {
    jQuery("#pluginInfo").empty();
    jQuery("#pluginUploadMsg").hide();
    var xhr = new XMLHttpRequest();
    if (xhr.upload) {

        jQuery("#progressbar").show();
        xhr.upload.addEventListener("progress", function(e) {
            var pc = parseInt((e.loaded / e.total) * 100);
            jQuery("#progressbar").progressbar("value", pc);
        }, false);

        // file received/failed
        xhr.onreadystatechange = function(e) {
            if (xhr.readyState == 4) {
                jQuery("#progressbar").hide();
                if (xhr.status == 200){
                    showMessage("Plugin " + file.name + " sucessfully uploaded.");
                    var response = xhr.responseText;
                    var pluginName = response.split(" ")[0];
                    jQuery("#pluginInfo").load('plugins/' + pluginName);
                    jQuery('#dialog-upload').dialog("close");
                    setButtonsVisibility(true);
                    var pluginItem = '<li class="ui-widget-content" title="' + pluginName + '">' + pluginName + '</li>';
                    jQuery(pluginItem).appendTo(jQuery('#selectable'));

                // Add the plugin to the selectable
                }else{
                    showMessage(xhr.responseText, true, true);
                }
            }
        };

        // start upload
        xhr.open("POST", "uploadPlugin", true);
        var formData = new FormData();
        formData.append("file", file);
        xhr.send(formData);
    }
}

function selectSelectableElement (selectableContainer, elementToSelect){
    // add unselecting class to all elements in the styleboard canvas except current one
    jQuery("li", selectableContainer).each(function() {
        if (this != elementToSelect[0])
            jQuery(this).removeClass("ui-selected").addClass("ui-unselecting");
    });

    // add ui-selecting class to the element to select
    elementToSelect.addClass("ui-selecting");

    // trigger the mouse stop event (this will select all .ui-selecting elements, and deselect all .ui-unselecting elements)
    selectableContainer.data("selectable")._mouseStop(null);
}


 



