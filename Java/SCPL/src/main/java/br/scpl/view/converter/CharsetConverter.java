package br.scpl.view.converter;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import com.beust.jcommander.IStringConverter;

public class CharsetConverter implements IStringConverter<Charset> {

	@Override
	public Charset convert(String value) {
		
		return Charset.forName(value);
	}
}
