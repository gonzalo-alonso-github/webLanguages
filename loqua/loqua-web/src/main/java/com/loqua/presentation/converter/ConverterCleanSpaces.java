package com.loqua.presentation.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Define un conversor encargado de eliminar los espacios en blanco
 * al principio y al final del valor del componente UIComponent
 * sobre el que se aplica
 * @author Gonzalo
 */
@FacesConverter("converterCleanSpaces")
public class ConverterCleanSpaces implements Converter {

	@Override
	public Object getAsObject(
			FacesContext context, UIComponent component, String value) {
		value = value.toString().replaceAll(" +$", "");
		value = value.toString().replaceAll("^ +", "");
		value = value.toString().replaceAll("\\h{2,}", " ");
		value = value.toString().replaceAll("\\v{2,}", "\n");
		return value;
	}

	@Override
	public String getAsString(
			FacesContext context, UIComponent component, Object value) {
		return (String) value;
	}
}
