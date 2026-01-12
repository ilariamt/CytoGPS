package main;

/**
 * @author Lin Zhang
 * Programmer / Software Development Engineer
 * Institute for Informatics
 * Washington University School of Medicine in St. Louis
 * 
 * Date: August 30, 2021 
 */
public class TokenError {
	private int offsetStart;
	private int length;
	private String message;
	
	public TokenError(int offsetStart, int length, String message) {			
		this.offsetStart = offsetStart;
		this.length = length;
		this.message = message;
	}
	
	public String toString(String input) {
		try {
			// Safely extract substring, ensuring indices don't exceed input length
			int start = Math.max(0, offsetStart);
			int end = Math.min(input.length(), offsetStart + length);
			
			if (start >= input.length()) {
				// If offset is beyond input, show the full input (truncated if too long)
				String display = input.length() > 100 ? input.substring(Math.max(0, input.length() - 100)) : input;
				return "[Full input: " + display + "]: " + message;
			}
			
			// Show the error context: substring with some padding
			String errorPart = input.substring(start, end);
			if (errorPart.length() > 100) {
				errorPart = errorPart.substring(0, 100) + "...";
			}
			return errorPart + ": " + message;
		} catch (Exception e) {
			// Fallback: show the full input if something goes wrong
			String display = input.length() > 100 ? input.substring(Math.max(0, input.length() - 100)) : input;
			return "[Full input near error: " + display + "]: " + message;
		}
	}

	public int getOffsetStart() {
		return offsetStart;
	}

	public void setOffsetStart(int offsetStart) {
		this.offsetStart = offsetStart;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
