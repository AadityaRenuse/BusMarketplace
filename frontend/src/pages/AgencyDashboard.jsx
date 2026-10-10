
import { useEffect, useState } from "react";
import "./AgencyDashboard.css";

function AgencyDashboard({ onMyBuses }) {
  const [trips, setTrips] = useState([]);
  const [bids, setBids] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bidsLoading, setBidsLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [selectedTrip, setSelectedTrip] = useState(null);
  const [buses, setBuses] = useState([]);
  const [bidAmount, setBidAmount] = useState("");
  const [selectedBusId, setSelectedBusId] = useState("");
  const [bidMessage, setBidMessage] = useState("");
  const [activeSection, setActiveSection] = useState("trips");

  /*
   * Load open trips
   */
  useEffect(() => {
    fetch("http://localhost:8080/api/trip-requests")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch trips");
        }

        return response.json();
      })
      .then((data) => {
        const openTrips = data.filter(
          (trip) => trip.status === "OPEN"
        );

        setTrips(openTrips);
        setLoading(false);
      })
      .catch((error) => {
        console.error(error);
        setMessage("Could not load available trips.");
        setLoading(false);
      });
  }, []);

  /*
   * Load only buses belonging to the logged-in agency
   */
  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      return;
    }

    fetch("http://localhost:8080/api/buses/my", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch your buses");
        }

        return response.json();
      })
      .then((data) => {
        setBuses(data);
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  /*
   * Load agency's own bids
   */
  const loadMyBids = () => {
    const token = localStorage.getItem("token");

    if (!token) {
      alert("Please login as an agency.");
      return;
    }

    setBidsLoading(true);

    fetch("http://localhost:8080/api/bids/my", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch your bids");
        }

        return response.json();
      })
      .then((data) => {
        setBids(data);
        setBidsLoading(false);
      })
      .catch((error) => {
        console.error(error);
        setBidsLoading(false);
      });
  };

  /*
   * Submit bid
   */
  const submitBid = () => {
    const token = localStorage.getItem("token");

    if (!token) {
      alert("Please login as an agency.");
      return;
    }

    if (!selectedBusId) {
      alert("Please select a bus.");
      return;
    }

    if (!bidAmount) {
      alert("Please enter a bid amount.");
      return;
    }

    const bid = {
      tripRequestId: selectedTrip.tripRequestId,
      busId: Number(selectedBusId),
      amount: Number(bidAmount),
      message: bidMessage,
      status: "PENDING",
    };

    fetch("http://localhost:8080/api/bids", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(bid),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to submit bid");
        }

        return response.json();
      })
      .then((data) => {
        console.log("Bid submitted:", data);

        alert("Bid submitted successfully!");

        setSelectedTrip(null);
        setSelectedBusId("");
        setBidAmount("");
        setBidMessage("");

        // Refresh My Bids if needed
        loadMyBids();
      })
      .catch((error) => {
        console.error(error);
        alert("Could not submit bid.");
      });
  };

  /*
   * Open My Bids section
   */
  const showMyBids = () => {
    setActiveSection("bids");
    loadMyBids();
  };

  /*
   * Open Available Trips section
   */
  const showAvailableTrips = () => {
    setActiveSection("trips");
  };

  return (
    <div className="agency-dashboard">

      <div className="agency-header">

        <p className="tagline">
          AGENCY MARKETPLACE
        </p>

        <h1>
          Agency Dashboard
        </h1>

        <p>
          Manage available trips and track the bids submitted
          by your agency.
        </p>

      </div>

      {/* Dashboard navigation */}

      <div className="agency-dashboard-nav">

        <button
          className={
            activeSection === "trips"
              ? "dashboard-nav-btn active"
              : "dashboard-nav-btn"
          }
          onClick={showAvailableTrips}
        >
          Available Trips
        </button>

        <button
          className={
            activeSection === "bids"
              ? "dashboard-nav-btn active"
              : "dashboard-nav-btn"
          }
          onClick={showMyBids}
        >
          My Bids
        </button>



        <button
          className="dashboard-nav-btn"
          onClick={onMyBuses}
        >
          My Buses
        </button>


      </div>

      {/* ========================= */}
      {/* AVAILABLE TRIPS */}
      {/* ========================= */}

      {activeSection === "trips" && (
        <>
          {loading && (
            <div className="agency-message">
              Loading available trips...
            </div>
          )}

          {message && (
            <div className="agency-message error">
              {message}
            </div>
          )}

          {!loading && !message && trips.length === 0 && (
            <div className="agency-message">
              There are currently no open trips.
            </div>
          )}

          {selectedTrip && (
            <div className="bid-form">

              <h2>
                Submit Bid
              </h2>

              <p>
                <strong>
                  {selectedTrip.pickupLocation}
                  {" → "}
                  {selectedTrip.destination}
                </strong>
              </p>

              <p>
                Travel dates: {selectedTrip.startDate} to{" "}
                {selectedTrip.endDate}
              </p>

              <label>
                Select Bus
              </label>

              <select
                value={selectedBusId}
                onChange={(e) =>
                  setSelectedBusId(e.target.value)
                }
              >
                <option value="">
                  Select a bus
                </option>

                {buses.map((bus) => (
                  <option
                    key={bus.busId}
                    value={bus.busId}
                  >
                    {bus.busName} - {bus.capacity} seats
                  </option>
                ))}
              </select>

              <label>
                Bid Amount
              </label>

              <input
                type="number"
                placeholder="Enter your bid amount"
                value={bidAmount}
                onChange={(e) =>
                  setBidAmount(e.target.value)
                }
              />

              <label>
                Message
              </label>

              <textarea
                placeholder="Enter a message for the customer"
                value={bidMessage}
                onChange={(e) =>
                  setBidMessage(e.target.value)
                }
              />

              <div className="bid-form-buttons">

                <button
                  className="submit-bid-btn"
                  onClick={submitBid}
                >
                  Submit Bid
                </button>

                <button
                  className="cancel-bid-btn"
                  onClick={() => {
                    setSelectedTrip(null);
                    setSelectedBusId("");
                    setBidAmount("");
                    setBidMessage("");
                  }}
                >
                  Cancel
                </button>

              </div>

            </div>
          )}

          <div className="agency-trip-grid">

            {trips.map((trip) => (

              <div
                className="agency-trip-card"
                key={trip.tripRequestId}
              >

                <div className="agency-trip-header">

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

                  <span className="open-status">
                    OPEN
                  </span>

                </div>

                <div className="agency-trip-info">

                  <div>
                    <span>
                      Start Date
                    </span>

                    <strong>
                      {trip.startDate}
                    </strong>
                  </div>

                  <div>
                    <span>
                      End Date
                    </span>

                    <strong>
                      {trip.endDate || "Not specified"}
                    </strong>
                  </div>

                  <div>
                    <span>
                      Passengers
                    </span>

                    <strong>
                      {trip.passengers || "Not specified"}
                    </strong>
                  </div>

                </div>

                <div className="requirements">

                  <span>
                    Requirements
                  </span>

                  <p>
                    {trip.requirements ||
                      "No special requirements"}
                  </p>

                </div>

                <button
                  className="submit-bid-btn"
                  onClick={() => setSelectedTrip(trip)}
                >
                  Submit Bid
                </button>

              </div>

            ))}

          </div>
        </>
      )}

      {/* ========================= */}
      {/* MY BIDS */}
      {/* ========================= */}

      {activeSection === "bids" && (
        <>
          <div className="my-bids-header">
            <h2>
              My Bids
            </h2>

            <p>
              View all bids submitted by your agency.
            </p>
          </div>

          {bidsLoading && (
            <div className="agency-message">
              Loading your bids...
            </div>
          )}

          {!bidsLoading && bids.length === 0 && (
            <div className="agency-message">
              You have not submitted any bids yet.
            </div>
          )}

          {!bidsLoading && bids.length > 0 && (
            <div className="agency-bids-grid">

              {bids.map((bid) => (

                <div
                  className="agency-bid-card"
                  key={bid.bidId}
                >

                  <div className="agency-bid-header">

                    <div>
                      <span className="trip-id">
                        Bid #{bid.bidId}
                      </span>

                      <h2>
                        Trip #{bid.tripRequestId}
                      </h2>
                    </div>

                    <span
                      className={
                        bid.status === "ACCEPTED"
                          ? "bid-status accepted"
                          : bid.status === "REJECTED"
                          ? "bid-status rejected"
                          : "bid-status pending"
                      }
                    >
                      {bid.status}
                    </span>

                  </div>

                  <div className="agency-bid-info">

                    <div>
                      <span>
                        Bus ID
                      </span>

                      <strong>
                        #{bid.busId}
                      </strong>
                    </div>

                    <div>
                      <span>
                        Bid Amount
                      </span>

                      <strong>
                        ₹{Number(bid.amount).toLocaleString("en-IN")}
                      </strong>
                    </div>

                    <div>
                      <span>
                        Submitted
                      </span>

                      <strong>
                        {bid.createdAt
                          ? new Date(
                              bid.createdAt
                            ).toLocaleDateString("en-IN")
                          : "—"}
                      </strong>
                    </div>

                  </div>

                  <div className="requirements">

                    <span>
                      Message
                    </span>

                    <p>
                      {bid.message ||
                        "No message provided"}
                    </p>

                  </div>

                </div>

              ))}

            </div>
          )}
        </>
      )}

    </div>
  );
}

export default AgencyDashboard;
