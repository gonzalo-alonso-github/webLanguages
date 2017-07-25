var allCheckButtonMessages = new Array();

$(document).ready(function(){
	// para mostrar los tooltip:
	$('[data-toggle="tooltip"]').tooltip();
	
	// en el array 'allCheckButtonMessages'
	// guardamos los inputs que tengan type='checkbox' y cuyo nombre comience por 'checkButton'
	var allInputTags = new Array();
	allInputTags = document.getElementsByTagName('input');
	for( var i=0; i<allInputTags.length; i++ ){
		if( allInputTags[i].type == "checkbox" &&  allInputTags[i].name.substring(0,11) == "chk_message"){
			allCheckButtonMessages.push(allInputTags[i]);
		}
	}
});	
	
var statusButtonReceivedMessages = true;
var statusButtonSentMessages = false;

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
		case 'buttonReceivedMessages':
			statusButtonReceivedMessages = (statusButtonReceivedMessages == true) ? false : true
			// cambiar el estilo del boton:
			changeButtonStyle( buttonId, statusButtonReceivedMessages );
			
			// si hemos activado este boton, y el buttonSentMessages estaba activado, desactivalo ahora:
			if(  statusButtonReceivedMessages==true &&  statusButtonSentMessages==true ){
				statusButtonSentMessages = false;
				changeButtonStyle( 'buttonSentMessages', statusButtonSentMessages );
			}
			
			var tableToShow='tableReceivedMessages';
			var tableToHide='tableSentMessages';
			showCurrentTable(tableToShow, tableToHide);
			
			break;
		
		case 'buttonSentMessages':
			statusButtonSentMessages = (statusButtonSentMessages == true) ? false : true
			// cambiar el estilo del boton:
			changeButtonStyle( buttonId, statusButtonSentMessages );
			
			// si hemos activado este boton, y el buttonReceivedMessages estaba activado, desactivalo ahora:
			if(  statusButtonSentMessages==true &&  statusButtonReceivedMessages==true ){
				statusButtonReceivedMessages = false;
				changeButtonStyle( 'buttonReceivedMessages', statusButtonReceivedMessages );
			}
			
			var tableToShow='tableSentMessages';
			var tableToHide='tableReceivedMessages';
			showCurrentTable(tableToShow, tableToHide);
			
			break;
	}
}

function selectAllMessages( inputIsChecked ){
	if( inputIsChecked ){
		for( var i=0; i<allCheckButtonMessages.length; i++ ){
			allCheckButtonMessages[i].checked=true;
		}
	}else{
		for( var i=0; i<allCheckButtonMessages.length; i++ ){
			allCheckButtonMessages[i].checked=false;
		}
	}
}

function selectAllMessagesWithKeyboard( event, input ){
	// Este if comprueba que se haya tecleado ENTER
	// IE no soporta event.keyCode
	if (event.which == 13 || event.keyCode == 13) {
		if( input.checked==true ){
			input.checked=false;
			for( var i=0; i<allCheckButtonMessages.length; i++ ){
				allCheckButtonMessages[i].checked=false;
			}
		}else{
			input.checked=true;
			for( var i=0; i<allCheckButtonMessages.length; i++ ){
				allCheckButtonMessages[i].checked=true;
			}
		}
	}
}