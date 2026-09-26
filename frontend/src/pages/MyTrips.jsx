import { useEffect, useState } from "react";
import "./MyTrips.css";

function MyTrips({ onViewBids }) {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("Please login to view your trips.");
      setLoading(false);
      return;
    }

    fetch("http://localhost:8080/api/trip-requests/my", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch your trips");
        }

        return response.json();
      })
      .then((data) => {
        setTrips(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error(error);
        setMessage("Could not load your trips.");
        setLoading(false);
      });
  }, []);

  return (
    <div className="my-trips-page">
      <div className="my-trips-header">
        <p className="tagline">YOUR JOURNEYS</p>

        <h1>My Trips</h1>

        <p>
          View and manage the trips you have posted on BusMarket.
        </p>
      </div>

      {loading && (
        <div className="trips-message">
          Loading your trips...
        </div>
      )}

      {message && (
        <div className="trips-message error">
          {message}
        </div>
      )}

      {!loading && !message && trips.length === 0 && (
        <div className="trips-message">
          You haven't posted any trips yet.
        </div>
      )}

      <div className="trips-list">
        {trips.map((trip) => (
          <div className="trip-card" key={trip.tripRequestId}>
            <div className="trip-card-top">
              <div>
                <span className="trip-id">
                  Trip #{trip.tripRequestId}
                </span>

                <h2>
                  {trip.pickupLocation}
                  <span> → </span>
                  {trip.destination}
                </h2>
              </div>

              <span
                className={`trip-status ${trip.status?.toLowerCase()}`}
              >
                {trip.status}
              </span>
            </div>

            <div className="trip-info">
              <div>
                <span>Start Date</span>
                <strong>{trip.startDate}</strong>
              </div>

              <div>
                <span>End Date</span>
                <strong>{trip.endDate}</strong>
              </div>

              <div>
                <span>Passengers</span>
                <strong>{trip.passengers}</strong>
              </div>
            </div>

            <div className="trip-requirements">
              <span>Requirements</span>
              <p>{trip.requirements || "No special requirements"}</p>
            </div>

            <div className="trip-actions">
              <button className="view-trip-btn">
                View Details
              </button>

            <button
              className="view-bids-btn"
              onClick={() => onViewBids(trip.tripRequestId, trip.status)}
            >
              View Bids
            </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default MyTrips;