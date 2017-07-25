function setSpinnerInLayout( elementId ){
	var target = document.getElementById(elementId);
	var spinner = new Spinner().spin(target);
}