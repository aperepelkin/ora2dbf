package ru.ailabs.convert2dbf.converter;

public enum Converters {
	AS_IS(new AsIsConverter());

	private Converter<?, ?> converter;
	
	Converters(Converter<?, ?> converter) {
		this.converter = converter;
	}
	
	public Converter<?, ?> toConverter() {
		return converter;
	}
}
