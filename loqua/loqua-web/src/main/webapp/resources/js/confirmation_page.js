$(document).ready(function(){
	// mostrar la rueda 'spinner':
	var target = document.getElementById("spinnerWheel");
	var spinner = new Spinner().spin(target);
	
	// la accion de comprobar la url y actualizar la vista
	// se ejecutara nada mas cargar la pagina:
	document.getElementById('hidenButton').click();
});