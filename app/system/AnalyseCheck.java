package system;

/**
 * Class to verify stats of a analysed tweet
 * @author aers
 *
 */
public class AnalyseCheck {

	private String normalizedText;
	
	private float correctRate;
	
	private int totalAnalysed;
	
	private int totalCorrect;

	public AnalyseCheck(String normalizedText, float correctRate,
			int totalAnalysed, int totalCorrect) {
		super();
		this.normalizedText = normalizedText;
		this.correctRate = correctRate;
		this.totalAnalysed = totalAnalysed;
		this.totalCorrect = totalCorrect;
	}

	public String getNormalizedText() {
		return normalizedText;
	}

	public void setNormalizedText(String normalizedText) {
		this.normalizedText = normalizedText;
	}

	public float getCorrectRate() {
		return correctRate;
	}

	public void setCorrectRate(float correctRate) {
		this.correctRate = correctRate;
	}

	public int getTotalAnalysed() {
		return totalAnalysed;
	}

	public void setTotalAnalysed(int totalAnalysed) {
		this.totalAnalysed = totalAnalysed;
	}

	public int getTotalCorrect() {
		return totalCorrect;
	}

	public void setTotalCorrect(int totalCorrect) {
		this.totalCorrect = totalCorrect;
	}
}
