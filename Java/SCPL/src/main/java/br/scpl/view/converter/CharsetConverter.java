package br.scpl.view.converter;

import java.nio.charset.Charset;

import com.beust.jcommander.IStringConverter;

public class CharsetConverter implements IStringConverter<Charset> {

	@Override
	public Charset convert(String value) {
		
		return Charset.forName(value);
	}
}
