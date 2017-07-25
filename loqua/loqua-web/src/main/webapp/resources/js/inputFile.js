/*
Se utiliza para actualizar la vista previa de la imagen de perfil
subida por el usuario, haciendo:
	<o:graphicImage id="image_upload_preview" dataURI="true" 
		value="#{beanUserProfileImage.getLoggedUserImage()}" />
	<h:inputFile id="inputFile" value="#{beanUserEditProfile.imageProfile}"
			onchange="updateInputFile(this);">
        <f:ajax execute="@form :form_editAvatar" render="@form :form_editAvatar"/>
    </h:inputFile>
Como alternativa, podriamos usar un bean:
	<o:graphicImage id="image_upload_preview" dataURI="true" 
		value="#{beanUserProfileImage.getLoggedUserImage()}" />
	<h:inputFile id="inputFile" value="#{beanUserEditProfile.imageProfile}">
        <f:ajax execute="@form :form_editAvatar" render="@form :form_editAvatar"
        	listener="#{beanUserEditProfile.updateAvatarPreview()}" />
    </h:inputFile>
*/
function updateInputFile(input) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();
		reader.onload = function (e) {
			$('#image_upload_preview').attr('src', e.target.result);
		}
		reader.readAsDataURL(input.files[0]);
	}
};