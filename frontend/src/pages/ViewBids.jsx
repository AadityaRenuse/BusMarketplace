
import { useEffect, useState } from "react";
import "./ViewBids.css";

function ViewBids({ tripRequestId, tripStatus, onBack }) {
  const [bids, setBids] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [acceptingBid, setAcceptingBid] = useState(null);
  const [rejectingBid, setRejectingBid] = useState(null);

  const loadBids = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("Please login to view bids.");
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/bids/my-trip/${tripRequestId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch bids");
      }

      const data = await response.json();
      setBids(data);
      setMessage("");
    } catch (error) {
      console.error(error);
      setMessage("Could not load bids.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBids();
  }, [tripRequestId]);

  const handleAcceptBid = async (bid) => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("Please login to accept a bid.");
      return;
    }

    setAcceptingBid(bid.bidId);
    setMessage("");

    try {
      const response = await fetch(
        `http://localhost:8080/api/bids/${bid.bidId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            status: "ACCEPTED",
          }),
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Failed to accept bid");
      }

      setMessage(`Bid #${bid.bidId} accepted successfully!`);

      await loadBids();
    } catch (error) {
      console.error(error);
      setMessage(error.message || "Could not accept the bid.");
    } finally {
      setAcceptingBid(null);
    }
  };

  const handleRejectBid = async (bid) => {
    const token = localStorage.getItem("token");

    if (!token) {
      setMessage("Please login to reject a bid.");
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to reject Bid #${bid.bidId}?`
    );

    if (!confirmed) return;

    setRejectingBid(bid.bidId);
    setMessage("");

    try {
      const response = await fetch(
        `http://localhost:8080/api/bids/${bid.bidId}/reject`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Failed to reject bid");
      }

      const rejectedBid = await response.json();

      setBids((previousBids) =>
        previousBids.map((item) =>
          item.bidId === rejectedBid.bidId
            ? rejectedBid
            : item
        )
      );

      setMessage(`Bid #${rejectedBid.bidId} rejected successfully!`);
    } catch (error) {
      console.error(error);
      setMessage(error.message || "Could not reject the bid.");
    } finally {
      setRejectingBid(null);
    }
  };

  return (
    <div className="view-bids-page">
      <div className="view-bids-header">
        <button className="back-btn" onClick={onBack}>
          ← Back to My Trips
        </button>

        <p className="tagline">COMPETING OFFERS</p>

        <h1>Available Bids</h1>

        <p>
          Compare offers from bus operators and choose the one
          that works best for your trip.
        </p>
      </div>

      {loading && (
        <div className="bids-message">
          Loading bids...
        </div>
      )}

      {message && (
        <div className="bids-message">
          {message}
        </div>
      )}

      {!loading && bids.length === 0 && (
        <div className="bids-message">
          No bids have been submitted for this trip yet.
        </div>
      )}

      <div className="bids-grid">
        {bids.map((bid) => (
          <div className="bid-card" key={bid.bidId}>
            <div className="bid-card-header">
              <div>
                <span className="bid-id">
                  Bid #{bid.bidId}
                </span>

                <h2>Agency #{bid.agencyId}</h2>
              </div>

              <span
                className={`bid-status ${bid.status?.toLowerCase()}`}
              >
                {bid.status}
              </span>
            </div>

            <div className="bid-price">
              <span>Bid Amount</span>
              <strong>
                ₹{Number(bid.amount).toLocaleString("en-IN")}
              </strong>
            </div>

            <div className="bid-details">
              <div>
                <span>Bus</span>
                <strong>Bus #{bid.busId}</strong>
              </div>

              <div>
                <span>Trip Request</span>
                <strong>#{bid.tripRequestId}</strong>
              </div>
            </div>

            <div className="bid-message">
              <span>Operator Message</span>
              <p>
                {bid.message || "No message provided."}
              </p>
            </div>

            {bid.status === "PENDING" &&
              tripStatus !== "BOOKED" && (
                <div className="bid-actions">
                  <button
                    className="accept-bid-btn"
                    onClick={() => handleAcceptBid(bid)}
                    disabled={
                      acceptingBid !== null ||
                      rejectingBid !== null
                    }
                  >
                    {acceptingBid === bid.bidId
                      ? "Accepting..."
                      : "Accept Bid"}
                  </button>

                  <button
                    className="reject-bid-btn"
                    onClick={() => handleRejectBid(bid)}
                    disabled={
                      acceptingBid !== null ||
                      rejectingBid !== null
                    }
                  >
                    {rejectingBid === bid.bidId
                      ? "Rejecting..."
                      : "Reject Bid"}
                  </button>
                </div>
              )}

            {bid.status === "PENDING" &&
              tripStatus === "BOOKED" && (
                <div className="accepted-label">
                  Trip Already Booked
                </div>
              )}

            {bid.status === "ACCEPTED" && (
              <div className="accepted-label">
                ✓ Accepted Bid
              </div>
            )}

            {bid.status === "REJECTED" && (
              <div className="rejected-label">
                ✕ Rejected Bid
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default ViewBids;
