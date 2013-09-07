package models;

public class GeoLocation {
	
	private double lontitude;
	private double latitude;
	
	public GeoLocation(double lontitude, double latitude) {
		this.setLontitude(lontitude);
		this.setLatitude(latitude);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLontitude() {
		return lontitude;
	}

	public void setLontitude(double lontitude) {
		this.lontitude = lontitude;
	}
	
	
}