import { useEffect, useState } from "react";
import "./ViewBids.css";

function ViewBids({ tripRequestId, onBack }) {
  const [bids, setBids] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [acceptingBid, setAcceptingBid] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/api/bids")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch bids");
        }

        return response.json();
      })
      .then((data) => {
        const tripBids = data.filter(
          (bid) => bid.tripRequestId === tripRequestId
        );

        setBids(tripBids);
        setLoading(false);
      })
      .catch((error) => {
        console.error(error);
        setMessage("Could not load bids.");
        setLoading(false);
      });
  }, [tripRequestId]);


const handleAcceptBid = async (bid) => {
  setAcceptingBid(bid.bidId);
  setMessage("");

  try {
    const response = await fetch(
      `http://localhost:8080/api/bids/${bid.bidId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          tripRequestId: bid.tripRequestId,
          agencyId: bid.agencyId,
          busId: bid.busId,
          amount: bid.amount,
          message: bid.message,
          status: "ACCEPTED",
        }),
      }
    );

    if (!response.ok) {
      throw new Error("Failed to accept bid");
    }

    const updatedBid = await response.json();

    setBids((previousBids) =>
      previousBids.map((item) =>
        item.bidId === updatedBid.bidId
          ? updatedBid
          : item
      )
    );

    setMessage(
      `Bid #${updatedBid.bidId} accepted successfully!`
    );
  } catch (error) {
    console.error(error);
    setMessage("Could not accept the bid.");
  } finally {
    setAcceptingBid(null);
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
        <div className="bids-message error">
          {message}
        </div>
      )}

      {!loading && !message && bids.length === 0 && (
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
              <strong>₹{bid.amount}</strong>
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

            {bid.status === "PENDING" && (
            <button
                className="accept-bid-btn"
                onClick={() => handleAcceptBid(bid)}
                disabled={acceptingBid === bid.bidId}
            >
                {acceptingBid === bid.bidId
                ? "Accepting..."
                : "Accept Bid"}
            </button>
            )}

            {bid.status === "ACCEPTED" && (
              <div className="accepted-label">
                ✓ Accepted Bid
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default ViewBids;