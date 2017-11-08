
public class Bug {
	
	private Method location;
	private Method error;
	private Pair pair;
	public double getSupport() {
		return support;
	}
	public void setSupport(double support) {
		this.support = support;
	}
	public double getConfidence() {
		return confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	private double support;
	private double confidence;
	
	Bug(Method error,Method location, Pair pair, double support, double confidence ){
		this.error= error;
		this.location= location;
		this.pair = pair;
		this.support=support;
		this.confidence= confidence;
	}
	public Method getLocation() {
		return location;
	}
	public void setLocation(Method location) {
		this.location = location;
	}
	public Method getError() {
		return error;
	}
	public void setError(Method error) {
		this.error = error;
	}
	public Pair getPair() {
		return pair;
	}
	public void setPair(Pair pair) {
		this.pair = pair;
	}


}
