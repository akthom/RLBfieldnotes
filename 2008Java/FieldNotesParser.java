import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldNotesParser {

	public static void main(String[] args) throws Exception {
		
		DateFormat notesDateFormat = new SimpleDateFormat("d MMMMM yyyy");
		DateFormat recordDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Pattern patName = Pattern.compile("([A-Z])\\. ([A-Z])\\. (\\w)\\w+\\:\\s*");
		Pattern patProject = Pattern.compile("([A-Z][A-Z][A-Z]) (\\w+)\\s+(\\d+)/(.+)");
		Pattern patBd = Pattern.compile("BD\\s+=\\s+(.+?)( to (.+?))?\\s*(\\w to \\w)?");
		Pattern patBd3 = Pattern.compile("BD\\s+=\\s+(.+?)\\s+(.+?)\\s+(.+?)");
		Pattern patN = Pattern.compile("N\\s+=\\s+(.+?)( to (.+?))?");
		Pattern patN3 = Pattern.compile("N\\s+=\\s+(.+?)\\s+(.+?)\\s+(.+?)");
		Pattern patW = Pattern.compile("W\\s+=\\s+(.+?)( to (.+?))?");
		Pattern patW3 = Pattern.compile("W\\s+=\\s+(.+?)\\s+(.+?)\\s+(.+?)");
		Pattern patPoints = Pattern.compile("\\s*(\\w+)\\s+(\\w+)\\s+(.+)");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter out = new BufferedWriter(new FileWriter("out.csv"));
		
		out.write("ExcavatorID,Date,Notes,Project,GridNumber,PitNumb,GridCoord,Point1,Point2,Point3,BD1Ft,BD1In,BD2Ft,BD2In,BD3Ft,BD3In,BDOrient,N1,N2,N3,Norient,W1,W2,W3,Worient,FieldID\n");																																																																																																																																																																																																																																					
		
		String line;
		Record r = null;
		int lineNumber = 0;
		while ( (line = in.readLine()) != null) {
			lineNumber++;
			line = line.trim();
			if (line.length() == 0 || "7".equals(line) || line.endsWith("=")) {
				continue;
			}
			if (line.charAt(0) == 65279) {
				line = line.substring(1);
			}
			int i = line.indexOf(',');
			if (i > -1) {
				try {
					Date date = notesDateFormat.parse(line.substring(0, i));
					if (r != null) {
						r.print(out);
						out.flush();
					}
					r = new Record();
					r.date = recordDateFormat.format(date);
					continue;
				} catch (ParseException e) {
				}
			}
			Matcher m = patName.matcher(line);
			if (m.matches()) {
				if (!r.excavator.equals("")) {
					r.print(out);
					out.flush();
					Record last = r;
					r = new Record();
					r.date = last.date;
				}
				r.excavator = m.group(1) + m.group(2) + m.group(3);
				continue;
			}
			m = patProject.matcher(line);
			if (m.matches()) {
				r.print(out);
				out.flush();
				Record last = r;
				r = new Record();
				r.excavator = last.excavator;
				r.date = last.date;
				r.project = m.group(1);
				r.gridNumber = m.group(2);
				r.pitNumber = m.group(3);
				r.gridCoordinate = m.group(4);
				continue;
			}
			if (r.threePoint) {
				m = patBd3.matcher(line);
				if (m.matches()) {
					String[] s = parseMeasurement(m.group(1));
					r.bd1Feet = s[0];
					r.bd1Inches = s[1];
					s = parseMeasurement(m.group(2));
					r.bd2Feet = s[0];
					r.bd2Inches = s[1];
					s = parseMeasurement(m.group(3));
					r.bd3Feet = s[0];
					r.bd3Inches = s[1];
					continue;
				}
				m = patN3.matcher(line);
				if (m.matches()) {
					r.n1 = parseMeasurement(m.group(1))[1];
					r.n2 = parseMeasurement(m.group(2))[1];
					r.n3 = parseMeasurement(m.group(3))[1];
					continue;
				}
				m = patW3.matcher(line);
				if (m.matches()) {
					r.w1 = parseMeasurement(m.group(1))[1];
					r.w2 = parseMeasurement(m.group(2))[1];
					r.w3 = parseMeasurement(m.group(3))[1];
					continue;
				}
			} else {
				m = patBd.matcher(line);
				if (m.matches()) {
					String[] s = parseMeasurement(m.group(1));
					r.bd1Feet = s[0];
					r.bd1Inches = s[1];
					if (m.group(3) != null) {
						s = parseMeasurement(m.group(3));
						r.bd2Feet = s[0];
						r.bd2Inches = s[1];
						String orient = m.group(4);
						if (orient != null) {
							r.bdorient = orient;
						}
					}
					continue;
				}
				m = patN.matcher(line);
				if (m.matches()) {
					r.n1 = parseMeasurement(m.group(1))[1];
					if (m.group(3) != null) {
						r.n2 = parseMeasurement(m.group(3))[1];
					}
					continue;
				}
				m = patW.matcher(line);
				if (m.matches()) {
					r.w1 = parseMeasurement(m.group(1))[1];
					if (m.group(3) != null) {
						r.w2 = parseMeasurement(m.group(3))[1];
					}
					continue;
				}
			}
			if (r != null) {
				if (!(r.gridNumber.equals(""))) {
					if (!r.bd1Feet.equals("") || !r.bd1Inches.equals("") ||
							!r.n1.equals("") || !r.n2.equals(""))
					{
						if (r.fieldID.equals("")) {
							r.fieldID = line;
						} else {
							r.print(out);
							out.flush();
							Record last = r;
							r = new Record();
							r.excavator = last.excavator;
							r.date = last.date;
							r.notes = line;
						}
					} else {
						m = patPoints.matcher(line);
						if (m.matches() && line.length() < 25) {
							r.point1 = m.group(1);
							r.point2 = m.group(2);
							r.point3 = m.group(3);
							r.threePoint = true;
						} else {
							// This is where the "random text" ends up. :)
							//System.err.println(lineNumber + ": Unrecognized: " + line);
						}
					}
				} else if (r.notes.equals("")) {
					r.notes = line;
				} else {
					r.print(out);
					out.flush();
					Record last = r;
					r = new Record();
					r.excavator = last.excavator;
					r.date = last.date;
					r.notes = line;
				}
			}
		}
		out.flush();
		out.close();
		in.close();
	}

	private static String[] parseMeasurement(String line) {
		String[] rtn = new String[2];
		rtn[0] = "";
		rtn[1] = "";
		String measurement = "";
		for (int j = 0; j < line.length(); j++) {
			char c = line.charAt(j);
			if (Character.isDigit(c)) {
				measurement += c;
			} else if (c == 188) {	// 1/4
				measurement += ".25";
			} else if (c == 189) {	// 1/2
				measurement += ".5";
			} else if (c == 190) {	// 3/4
				measurement += ".75";
			} else if (c == 8217) {	// Feet
				rtn[0] = measurement;
				measurement = "";
			} else if (c == 8221) {	// Inches
				rtn[1] = measurement;
				measurement = "";
			}
		}
		return rtn;
	}
	
	private static class Record {
		boolean threePoint = false;
		String excavator = "";
		String date = "";
		String notes = "";
		String project = "";
		String gridNumber = "";
		String pitNumber = "";
		String gridCoordinate = "";
		String point1 = "";
		String point2 = "";
		String point3 = "";
		String bd1Feet = "";
		String bd1Inches = "";
		String bd2Feet = "";
		String bd2Inches = "";
		String bd3Feet = "";
		String bd3Inches = "";
		String bdorient = "";
		String n1 = "";
		String n2 = "";
		String n3 = "";
		String norient = "";
		String w1 = "";
		String w2 = "";
		String w3 = "";
		String worient = "";
		String fieldID = "";
		void print(Appendable out) throws IOException {
			out.append(excavator).append(",")
					.append(date).append(",");
			if (notes.length() > 0) {
				out.append("\"").append(notes).append("\"");
			}
			out.append(",");
			out.append(project).append(",")
					.append(gridNumber).append(",")
					.append(pitNumber).append(",")
					.append(gridCoordinate).append(",")
					.append(point1).append(",")
					.append(point2).append(",")
					.append(point3).append(",")
					.append(bd1Feet).append(",")
					.append(bd1Inches).append(",")
					.append(bd2Feet).append(",")
					.append(bd2Inches).append(",")
					.append(bd3Feet).append(",")
					.append(bd3Inches).append(",")
					.append(bdorient).append(",")
					.append(n1).append(",")
					.append(n2).append(",")
					.append(n3).append(",")
					.append(norient).append(",")
					.append(w1).append(",")
					.append(w2).append(",")
					.append(w3).append(",")
					.append(worient).append(",")
					.append(fieldID)
					.append("\n");
		}
	}
	
}
