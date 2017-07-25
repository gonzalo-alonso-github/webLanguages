
var statusButtonMostValued = false;
var statusButtonMostCommented = false;
var statusButtonMostValuedUser = true;
var statusButtonMostActiveUser = false;
// Boton del submenu superior:
var statusButtonThemes = false;

function changeButtonStyle( buttonId, currentStatus ){
	// cambiar el estilo del boton:
	if( currentStatus==true ){
		$('#'+buttonId).addClass('active'); // esto es JQuery
	}else{
		$('#'+buttonId).removeClass('active'); // esto es JQuery
	}
	
	//Solo para el boton del submenu superior
	if( buttonId == 'buttonThemes' ){
		statusButtonThemes = (statusButtonThemes == true) ? false : true
		// cambiar el estilo del boton:
		if( statusButtonThemes==true ){
			// $('#'+buttonId).addClass('active');
			$('#'+buttonId).addClass('general_header_button_active');
		}else{
			// $('#'+buttonId).removeClass('active');
			$('#'+buttonId).removeClass('general_header_button_active');
		}
	}
}

function changeButtonStatus( buttonId ){
	// guardar el nuevo estado del boton:
	switch (buttonId){
		case 'buttonMostValued':
			statusButtonMostValued = (statusButtonMostValued == true) ? false : true
			// cambiar el estilo del boton:
			changeButtonStyle( buttonId, statusButtonMostValued );
			break;
		case 'buttonMostCommented':
			statusButtonMostCommented = (statusButtonMostCommented == true) ? false : true
			// cambiar el estilo del boton:
			changeButtonStyle( buttonId, statusButtonMostCommented );
			break;
		case 'buttonMostValuedUser':
			statusButtonMostValuedUser = (statusButtonMostValuedUser == true) ? false : true
			// cambiar el estilo del boton:
			changeButtonStyle( buttonId, statusButtonMostValuedUser );
			
			// si hemos activado este boton, y el buttonMostActiveUser estaba activado, desactivalo ahora:
			if(  statusButtonMostValuedUser==true &&  statusButtonMostActiveUser==true ){
				statusButtonMostActiveUser = false;
				changeButtonStyle( 'buttonMostActiveUser', statusButtonMostActiveUser );
			}
			break;
		case 'buttonMostActiveUser':
			statusButtonMostActiveUser = (statusButtonMostActiveUser == true) ? false : true
			// cambiar el estilo del boton:
			changeButtonStyle( buttonId, statusButtonMostActiveUser );
			
			// si hemos activado este boton, y el buttonMostValuedUser estaba activado, desactivalo ahora:
			if(  statusButtonMostActiveUser==true &&  statusButtonMostValuedUser==true ){
				statusButtonMostValuedUser = false;
				changeButtonStyle( 'buttonMostValuedUser', statusButtonMostValuedUser );
			}
			break;
	}
}