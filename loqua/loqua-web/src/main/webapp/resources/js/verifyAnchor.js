// si el ancla (anchor) de la url apunta a una etiqueta
// que tiene el estilo "collapse" de bootstrap,
// se le agrega tambien el estilo "in"

$(document).ready(function(){
	var anchorElement = window.location.hash;
	// ejemplo: anchorElement = "referenceTo_querySuggestedCorrection307"
	var referencedElement = anchorElement.slice(13);
	// ejemplo: referencedElement = "querySuggestedCorrection307"
	if( $('#'+referencedElement).hasClass('collapse') ){
		$('#'+referencedElement).addClass('in');
		// var currentUrl = window.location; 
		// var currentUrl = window.location.href;
		// window.location.replace(currentUrl);
	}
});