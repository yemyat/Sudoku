import javax.swing.*;

public class GUIConsole {

	public static void display(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Display",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void display(double msg) {
		JOptionPane.showMessageDialog(null, "" + msg, "Display",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void display(int msg) {
		JOptionPane.showMessageDialog(null, "" + msg, "Display",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static Integer readInt(String prompt) {
		Integer value = null;
		boolean valid = false;
		while (!valid) {
			try {
				String input = JOptionPane.showInputDialog(null, prompt,
						"Input", JOptionPane.QUESTION_MESSAGE);
				if (input != null) {
					value = Integer.parseInt(input);
				}
				valid = true;
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Please enter an integer.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return value;
	}

	public static Double readDouble(String prompt) {
		Double value = null;
		boolean valid = false;
		while (!valid) {
			try {
				String input = JOptionPane.showInputDialog(null, prompt,
						"Input", JOptionPane.QUESTION_MESSAGE);
				if (input != null) {
					value = Double.parseDouble(input);
				}
				valid = true;
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Please enter an double.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return value;
	}

	public static Character readChar(String prompt) {
		Character value = null;
		boolean valid = false;
		while (!valid) {
			String input = readString(prompt);
			if (input.length() != 1) {
				JOptionPane.showMessageDialog(null,
						"Please enter a character.", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				value = input.charAt(0);
				valid = true;
			}
		}
		return value;
	}

	public static Boolean readBoolean(String prompt) {
		Boolean value = null;
		int input = JOptionPane.showConfirmDialog(null, prompt, "Input",
				JOptionPane.YES_NO_OPTION);
		if (input == JOptionPane.YES_OPTION) {
			value = true;
		} else if (input == JOptionPane.NO_OPTION) {
			value = false;
		}
		return value;
	}

	public static String readString(String prompt) {
		String value = JOptionPane.showInputDialog(null, prompt, "Input",
				JOptionPane.QUESTION_MESSAGE);
		return value;
	}

}
