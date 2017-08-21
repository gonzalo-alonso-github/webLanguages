
function getPlainTextCorrection( divSentenceId, inputHiddenId ){
	// Este metodo se llamara desde el evento onkeyup (pulsar tecla)
	
	divSentenceId = divSentenceId.replace(':','\\:');
	inputHiddenId = inputHiddenId.replace(':','\\:');
	/* Para devolver el texto plano de un elemento (p.ej. compilando "&nbsp;")
		... = $('#'+divSentenceId).text().trim();
	Para devolver el texto 'bruto' de un elemento (sin compilar los "&nbsp;")
		... = $('#'+divSentenceId).html().trim();
	Para convertir a texto 'bruto' el contenido de un elemento (no se usa aqui)
		... = document.getElementById(divSentenceId).innerHTML.trim(); */
	
	var compliledEditedText = $('#'+divSentenceId).text().trim();
	var rawEditedText = $('#'+divSentenceId).html();
	if( !compliledEditedText
			|| !compliledEditedText.length
			|| !compliledEditedText.trim().length ){
		// Si, en el momento de pulsar tecla, el contenido es cadena vacia:
		// 1: se 'limpia' todo el contenido html del div
		document.getElementById(divSentenceId).innerHTML = "";
		// 2: el inputHidden tambien queda como cadena vacia
		document.getElementById(inputHiddenId).value = "";
	}else{
		// Si, en el momento de pulsar tecla, el contenido NO es cadena vacia:
		// con cada tecla,  el inputHidden va actualizandose
		document.getElementById(inputHiddenId).value = compliledEditedText;
	}
}

function cleanCorrectionSpacesIfEmpty( divSentenceId, inputHiddenId ){
	// Este metodo se llamara desde el evento onblur (perder el foco)
	
	divSentenceId = divSentenceId.replace(':','\\:');
	inputHiddenId = inputHiddenId.replace(':','\\:');
	/* Para devolver el texto plano de un elemento (p.ej. compilando "&nbsp;")
		... = $('#'+divSentenceId).text().trim();
	Para devolver el texto 'bruto' de un elemento (sin compilar los "&nbsp;")
		... = $('#'+divSentenceId).html().trim();
	Para convertir a texto 'bruto' el contenido de un elemento (no se usa aqui)
		... = document.getElementById(divSentenceId).innerHTML.trim(); */
	
	var compliledEditedText = $('#'+divSentenceId).text().trim();
	var rawEditedText = $('#'+divSentenceId).html().trim();
	if( !compliledEditedText
			|| !compliledEditedText.length
			|| !compliledEditedText.trim().length ){
		// Si, en el momento de perder el foco, el contenido es cadena vacia:
		// 1: se 'limpia' todo el contenido html del div
		document.getElementById(divSentenceId).innerHTML = "";
		// 2: el inputHidden tambien queda como cadena vacia
		document.getElementById(inputHiddenId).value = "";
	}else{
		// Si, en el momento de perder el foco, el contenido NO es cadena vacia:
		// 1: se limpian los posibles espacios en blanco del div (con trim),
		// a la vez que se convierte el html a texto plano (con innerHTML),
		// para evitar inyeccion de codigo
		document.getElementById(divSentenceId).innerHTML = rawEditedText.trim();
		// 2: y el inputHidden se actualiza
		document.getElementById(inputHiddenId).value = compliledEditedText.trim();
	}
}

/*
function showIconResetSentence( spanIconResetId ){
	spanIconResetId = spanIconResetId.replace(':','\\:');
	$('#'+spanIconResetId).removeClass('forum_correction_iconReset_hide');
}

function hideIconResetSentence( spanIconResetId ){
	spanIconResetId = spanIconResetId.replace(':','\\:');
	// Solo se oculta el elemento si no tiene el focus:
	if( $('#'+spanIconResetId).is(':focus')==false ){
		$('#'+spanIconResetId).addClass('forum_correction_iconReset_hide');
	}
}
*/