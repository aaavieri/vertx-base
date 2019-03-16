package com.yjl.vertx.base.com.factory.config;

import com.yjl.vertx.base.com.anno.component.Config;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigFactory {

	public static Config getConfig(String key) {
		return new ConfigImpl(key);
	}

	static class ConfigImpl implements Config {

		private String value;

		ConfigImpl(String value) {
			this.value = value;
		}

		@Override
		public String value() {
			return this.value;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Config.class;
		}
		public int hashCode() {
			return 127 * "value".hashCode() ^ this.value.hashCode();
		}

		public boolean equals(Object o) {
			if (!(o instanceof Config)) {
				return false;
			} else {
				Config other = (Config)o;
				return this.value.equals(other.value());
			}
		}
	}
}
