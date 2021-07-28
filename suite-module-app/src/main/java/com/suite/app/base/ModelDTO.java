package com.suite.app.base;

import com.suite.app.util.Utils;

public class ModelDTO {

	
	@Override
	public String toString() {

		return Utils.objectToJson(this);

	}
}
