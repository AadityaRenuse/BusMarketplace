import { useEffect, useState } from "react";
import "./FindBus.css";

function FindBus() {
  const [buses, setBuses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [selectedBus, setSelectedBus] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/api/buses")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch buses");
        }

        return response.json();
      })
      .then((data) => {
        setBuses(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error(error);
        setMessage(
          "Could not load buses. Make sure the backend is running."
        );
        setLoading(false);
      });
  }, []);

  return (
    <div className="find-bus-page">
      <div className="find-bus-header">
        <p className="tagline">EXPLORE OUR MARKETPLACE</p>

        <h1>Find a Bus</h1>

        <p>
          Browse buses from verified travel operators and find the
          right vehicle for your journey.
        </p>
      </div>

      {loading && (
        <div className="loading-message">
          Loading available buses...
        </div>
      )}

      {message && (
        <div className="error-message">
          {message}
        </div>
      )}

      {!loading && !message && buses.length === 0 && (
        <div className="empty-message">
          No buses are currently available.
        </div>
      )}

{selectedBus && (
  <div className="selected-bus">
    <div className="selected-bus-header">
      <div>
        <p className="tagline">BUS DETAILS</p>
        <h2>{selectedBus.busName}</h2>
      </div>

      <button
        className="close-details-btn"
        onClick={() => setSelectedBus(null)}
      >
        ✕ Close
      </button>
    </div>

    <div className="selected-bus-info">
      <div>
        <span>Bus Type</span>
        <strong>{selectedBus.busType}</strong>
      </div>

      <div>
        <span>Capacity</span>
        <strong>{selectedBus.capacity} passengers</strong>
      </div>

      <div>
        <span>AC</span>
        <strong>{selectedBus.isAc ? "Yes" : "No"}</strong>
      </div>

      <div>
        <span>Price per Trip</span>
        <strong>₹{selectedBus.pricePerTrip}</strong>
      </div>

      <div>
        <span>Registration Number</span>
        <strong>{selectedBus.registrationNumber}</strong>
      </div>

      <div>
        <span>Agency ID</span>
        <strong>{selectedBus.agencyId}</strong>
      </div>
    </div>

    <p className="selected-bus-description">
      {selectedBus.description}
    </p>
  </div>
)}

      <div className="bus-grid">
        {buses.map((bus) => (
          <div className="bus-card" key={bus.busId}>
            <div className="bus-image">
              🚌
            </div>

            <div className="bus-card-content">
              <h3>
                {bus.busName || `Bus #${bus.busId}`}
              </h3>

              <div className="bus-details">
                <div>
                  <span>Type</span>
                  <strong>
                    {bus.busType || "Bus"}
                  </strong>
                </div>

                <div>
                  <span>Capacity</span>
                  <strong>
                    {bus.capacity
                      ? `${bus.capacity} passengers`
                      : "Not specified"}
                  </strong>
                </div>
              </div>

              <p>
                {bus.description ||
                  "Comfortable bus suitable for group travel."}
              </p>

              <button
                className="view-bus-btn"
                onClick={() => setSelectedBus(bus)}
                >       
                 View Details
            </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default FindBus;