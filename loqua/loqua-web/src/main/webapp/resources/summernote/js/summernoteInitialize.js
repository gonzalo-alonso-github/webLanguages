
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
	var plainText = $($('#'+summernote_element_id).summernote('code')).text();
	$('#'+plainText_element_id).val(plainText);
}

//Esta funcion no se utiliza, queda comentada como apunte para aprendizaje:
/*
function destroySummernote() {
	$('.summernote').summernote('destroy');
}
*/