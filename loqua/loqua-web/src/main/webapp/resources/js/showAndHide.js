// funciones JQuery que cambian propiedades de elementos de la vista

function hideElement( elementId ){
	elementId = elementId.replace(':','\\:');
	$('#'+elementId).addClass('general_noDisplay');
}
function showElement( elementId ){
	elementId = elementId.replace(':','\\:');
	$('#'+elementId).removeClass('general_noDisplay');
}
function removeText( elementId ){
	elementId = elementId.replace(':','\\:');
	$('#'+elementId).text("");
}
function setText( elementId, value ){
	elementId = elementId.replace(':','\\:');
	$('#'+elementId).text(value);
}
function removeTextValue( elementId ){
	elementId = elementId.replace(':','\\:');
	$('#'+elementId).val("");
}
function setTextValue( elementId, value ){
	elementId = elementId.replace(':','\\:');
	$('#'+elementId).val(value);
}
function setReadonlyTrue( elementId ){
	elementId = elementId.replace(':','\\:');
	$('#'+elementId).prop('readonly', true);
}
function enableElement( elementId ){
	elementId = elementId.replace(':','\\:');
	$('#'+elementId).prop('disabled', false);
}
function disableElement( elementId ){
	elementId = elementId.replace(':','\\:');
	$('#'+elementId).prop('disabled', true);
}