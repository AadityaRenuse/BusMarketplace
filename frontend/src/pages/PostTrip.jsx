import { useState } from "react";
import "./PostTrip.css";

function PostTrip() {
  const [formData, setFormData] = useState({
    customerId: 1,
    pickupLocation: "",
    destination: "",
    startDate: "",
    endDate: "",
    passengers: "",
    requirements: "",
  });

  const [message, setMessage] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setMessage("Posting your trip...");

    try {
      const response = await fetch(
        "http://localhost:8080/api/trip-requests",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            customerId: Number(formData.customerId),
            pickupLocation: formData.pickupLocation,
            destination: formData.destination,
            startDate: formData.startDate,
            endDate: formData.endDate,
            passengers: Number(formData.passengers),
            requirements: formData.requirements,
            status: "OPEN",
          }),
        }
      );

      if (!response.ok) {
        throw new Error("Failed to create trip request");
      }

      const data = await response.json();

      setMessage(
        `Trip posted successfully! Trip Request ID: ${data.tripRequestId}`
      );

      setFormData({
        customerId: 1,
        pickupLocation: "",
        destination: "",
        startDate: "",
        endDate: "",
        passengers: "",
        requirements: "",
      });
    } catch (error) {
      console.error(error);
      setMessage(
        "Could not post the trip. Make sure the backend is running."
      );
    }
  };

  return (
    <div className="post-trip-page">
      <div className="post-trip-container">
        <div className="post-trip-header">
          <p className="tagline">PLAN YOUR JOURNEY</p>

          <h1>Post a Trip</h1>

          <p>
            Tell us what you need and let verified bus operators compete
            with their best prices.
          </p>
        </div>

        <form className="trip-form" onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Pickup Location</label>
              <input
                type="text"
                name="pickupLocation"
                value={formData.pickupLocation}
                onChange={handleChange}
                placeholder="e.g. Pune"
                required
              />
            </div>

            <div className="form-group">
              <label>Destination</label>
              <input
                type="text"
                name="destination"
                value={formData.destination}
                onChange={handleChange}
                placeholder="e.g. Mumbai"
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Start Date</label>
              <input
                type="date"
                name="startDate"
                value={formData.startDate}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label>End Date</label>
              <input
                type="date"
                name="endDate"
                value={formData.endDate}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label>Number of Passengers</label>
            <input
              type="number"
              name="passengers"
              value={formData.passengers}
              onChange={handleChange}
              placeholder="e.g. 30"
              min="1"
              required
            />
          </div>

          <div className="form-group">
            <label>Requirements</label>
            <textarea
              name="requirements"
              value={formData.requirements}
              onChange={handleChange}
              placeholder="Tell operators what you need..."
              rows="5"
            />
          </div>

          <button type="submit" className="submit-trip-btn">
            Post Trip
          </button>

          {message && <p className="form-message">{message}</p>}
        </form>
      </div>
    </div>
  );
}

export default PostTrip;