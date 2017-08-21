
$(document).ready(function(){
	initSummernote();
});

function initSummernote(){
	// para mostrar la toolbar de Summernote de editor de texto:
	$('.summernote').summernote({
		height: 200,
		minHeight: null,
		maxHeight: null,
		focus: false,
		//disableDragAndDrop no puede desactivarse. Solucion: lo hago en el .css
		//disableDragAndDrop: false,
		toolbar: [
			// [groupName, [list of button]]
			['style', ['bold', 'italic', 'underline', 'clear']],
			['font', ['strikethrough', 'superscript', 'subscript']],
			['fontsize', ['fontsize']],
			['color', ['color']],
			['para', ['ul', 'ol']],
			['table', ['table']],
			['insert', ['link']], //se permite insertar links, pero no imagenes
			//['insert', ['link', 'picture']],
			['action', ['undo', 'redo']],
			['misc', ['help']]
		]
	});
	//document.getElementById('summernote').innerHTML
	/*
	$('.summernote').on(
		'summernote.blur', function(customEvent, contents, $editable) {
			// esto seria el 'onblur' de summernote
    }).change();
    */
}

function reloadSummernoteFromAjaxOnevent(data) {
	//if (data.status == "success") {
	if (data.status == "complete") {
		initSummernote();
	}
}

function reloadSummernote() {
	initSummernote();
}

function getPlainText( summernote_element_id, plainText_element_id ) {
	var summernoteIsRendered = !!document.getElementById(summernote_element_id);
	if( ! summernoteIsRendered ){ return; }
	
	var rawText = $('#'+summernote_element_id).summernote('code');
	/* Cuando el usuario introduce varias lineas de separacion,
	summernote interpreta cada linea vacia como "<p><br></p>"
	Eso es innecesario, asi que se elimina aqui con la orden replace: */
	var find = '(<p><br></p>)+';
	var regExp = new RegExp(find, 'gi');
	rawText = rawText.replace(regExp, '');
	$('#'+summernote_element_id).val(rawText);
	/* Cuando el usuario introduce una sola linea de separacion,
	summernote interpreta el cambio linea como fin e inicio de parrafo:"</p><p>".
	Eso provoca que, al pasarlo a texto plano, se omiten esas etiquetas
	y erroneamente queda unida la ultima palabra de un parrafo
	con la primera palabra del siguiente. Para evitarlo,
	aqui se introduce un '\n' tras la ultima palabra de cada parrafo: */
	var find = '(</p>)';
	var regExp = new RegExp(find, 'gi');
	rawText = rawText.replace(regExp, '\\n</p>');
	// Tras esos arreglos ya se puede convertir a texto plano:
	var plainText = jQuery(rawText).text();
	$('#'+plainText_element_id).val(plainText);
}

//Esta funcion no se utiliza, queda comentada como apunte:
/*
function destroySummernote() {
	$('.summernote').summernote('destroy');
}
*/