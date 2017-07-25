
var statusButtonMostValuedUser = true;
var statusButtonMostActiveUser = false;

function changeButtonStyle( buttonId, currentStatus ){
	// cambiar el estilo del boton:
	if( currentStatus==true ){
		$('#'+buttonId).addClass('active'); // esto es JQuery
	}else{
		$('#'+buttonId).removeClass('active'); // esto es JQuery
	}
}

function showCurrentTable( tableToShow, tableToHide ){
	// tabla a ocultar:
	if( $('#'+tableToHide).hasClass('general_noDisplay')==false ){
		$('#'+tableToHide).addClass('general_noDisplay');
	}
	// tabla a mostrar:
	if( $('#'+tableToShow).hasClass('general_noDisplay') ){
		$('#'+tableToShow).removeClass('general_noDisplay');
	}
}

function changeButtonStatus( buttonId ){
	// guardar el nuevo estado del boton:
	switch (buttonId){
		case 'buttonMostValuedUser':
			statusButtonMostValuedUser = (statusButtonMostValuedUser == true) ? false : true
			// cambiar el estilo del boton:
			changeButtonStyle( buttonId, statusButtonMostValuedUser );
			
			// si hemos activado este boton, y el buttonMostActiveUser estaba activado, desactivalo ahora:
			if(  statusButtonMostValuedUser==true &&  statusButtonMostActiveUser==true ){
				statusButtonMostActiveUser = false;
				changeButtonStyle( 'buttonMostActiveUser', statusButtonMostActiveUser );
			}
			
			var tableToShow='tableMostValuedUser';
			var tableToHide='tableMostActiveUser';
			showCurrentTable(tableToShow, tableToHide);
			
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
			
			var tableToShow='tableMostActiveUser';
			var tableToHide='tableMostValuedUser';
			showCurrentTable(tableToShow, tableToHide);
			
			break;
	}
}