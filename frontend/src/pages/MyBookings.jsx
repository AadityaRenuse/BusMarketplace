
import { useEffect, useState } from "react";
import "./MyBookings.css";

function MyBookings() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("Please login to view your bookings.");
      setLoading(false);
      return;
    }

    fetch("http://localhost:8080/api/bookings/my", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch bookings");
        }

        return response.json();
      })
      .then((data) => {
        setBookings(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error(error);
        setMessage("Could not load your bookings.");
        setLoading(false);
      });
  }, []);

  return (
    <div className="my-bookings-page">
      <div className="my-bookings-header">
        <p className="tagline">YOUR RESERVATIONS</p>

        <h1>My Bookings</h1>

        <p>
          View all the bus bookings you have confirmed through
          BusMarket.
        </p>
      </div>

      {loading && (
        <div className="bookings-message">
          Loading your bookings...
        </div>
      )}

      {message && (
        <div className="bookings-message error">
          {message}
        </div>
      )}

      {!loading && !message && bookings.length === 0 && (
        <div className="bookings-message">
          You don't have any bookings yet.
        </div>
      )}

      <div className="bookings-list">
        {bookings.map((booking) => (
          <div
            className="booking-card"
            key={booking.bookingId}
          >
            <div className="booking-card-header">
              <div>
                <span className="booking-id">
                  Booking #{booking.bookingId}
                </span>

                <h2>
                  Trip #{booking.tripRequestId}
                </h2>
              </div>

              <span
                className={`booking-status ${booking.status?.toLowerCase()}`}
              >
                {booking.status}
              </span>
            </div>

            <div className="booking-info">
              <div>
                <span>Agency</span>
                <strong>Agency #{booking.agencyId}</strong>
              </div>

              <div>
                <span>Bus</span>
                <strong>Bus #{booking.busId}</strong>
              </div>

              <div>
                <span>Amount</span>
                <strong>
                  ₹{Number(booking.amount).toLocaleString("en-IN")}
                </strong>
              </div>

              <div>
                <span>Booking Type</span>
                <strong>{booking.bookingType}</strong>
              </div>

              <div>
                <span>Start Date</span>
                <strong>{booking.startDate}</strong>
              </div>

              <div>
                <span>End Date</span>
                <strong>{booking.endDate}</strong>
              </div>
            </div>

            <div className="booking-footer">
              <span>
                Created:{" "}
                {booking.createdAt
                  ? new Date(booking.createdAt).toLocaleString()
                  : "N/A"}
              </span>

              <span>
                Bid #{booking.bidId}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default MyBookings;
