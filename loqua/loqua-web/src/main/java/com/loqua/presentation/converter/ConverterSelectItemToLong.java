package com.loqua.presentation.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("converterSelectItemToLong")
public class ConverterSelectItemToLong implements Converter {

	@Override
	public Object getAsObject(
			FacesContext context, UIComponent component, String value) {
		Long languageID = Long.parseLong(value);
		return languageID;
	}

	@Override
	public String getAsString(
			FacesContext context, UIComponent component, Object value) {
		String result = "0"; 
		if( value!=null && value instanceof Long ){
			result = String.valueOf(((Long) value));
		}
		return result;
	}
}
