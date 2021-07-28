package com.suite.job.base;

public class EnumJob {

	/**
	 * Enummeradores para tipo de dados para banco
	 * @author Wenceslau
	 * 
	 */
	public enum Frequency {

		INTERVAL("INTERVAL"), 
		EVERYDAY("EVERYDAY"),
		EVERYWEEK("EVERYWEEK"), 
		EVERYMONTHLY("EVERYMONTHLY");

		private String frequency;

		private Frequency(String frequency) {
			this.frequency = frequency;
		}

		public String getFrequency() {
			return frequency;
		}

	}
	
	public enum State {

		WAIT("WAIT"),
		SLEEP("SLEEP"),
		RUNNING("RUNNING"), 
		FAILURE("FAILURE");

		private String state;

		private State(String state) {
			this.state = state;
		}

		public String getState() {
			return state;
		}

	}

}
