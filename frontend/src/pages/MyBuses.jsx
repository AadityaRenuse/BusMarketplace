
import { useEffect, useState } from "react";
import "./MyBuses.css";

function MyBuses() {
  const [buses, setBuses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [editingBusId, setEditingBusId] = useState(null);

  const [busName, setBusName] = useState("");
  const [registrationNumber, setRegistrationNumber] = useState("");
  const [capacity, setCapacity] = useState("");
  const [busType, setBusType] = useState("");
  const [isAc, setIsAc] = useState(true);
  const [pricePerTrip, setPricePerTrip] = useState("");
  const [description, setDescription] = useState("");

  const token = localStorage.getItem("token");

  /*
   * Load agency buses
   */
  const loadBuses = () => {
    if (!token) {
      setMessage("Please login as an agency.");
      setLoading(false);
      return;
    }

    fetch("http://localhost:8080/api/buses/my", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
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
        setMessage("Could not load your buses.");
        setLoading(false);
      });
  };

  useEffect(() => {
    loadBuses();
  }, []);

  /*
   * Reset form
   */
  const resetForm = () => {
    setBusName("");
    setRegistrationNumber("");
    setCapacity("");
    setBusType("");
    setIsAc(true);
    setPricePerTrip("");
    setDescription("");
    setEditingBusId(null);
    setShowForm(false);
  };

  /*
   * Open form for editing
   */
  const startEditing = (bus) => {
    setEditingBusId(bus.busId);
    setBusName(bus.busName || "");
    setRegistrationNumber(bus.registrationNumber || "");
    setCapacity(bus.capacity || "");
    setBusType(bus.busType || "");
    setIsAc(bus.isAc ?? true);
    setPricePerTrip(bus.pricePerTrip || "");
    setDescription(bus.description || "");
    setShowForm(true);

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  /*
   * Submit add/update form
   */
  const handleSubmit = () => {
    if (!token) {
      alert("Please login as an agency.");
      return;
    }

    if (!busName.trim()) {
      alert("Please enter the bus name.");
      return;
    }

    if (!registrationNumber.trim()) {
      alert("Please enter the registration number.");
      return;
    }

    if (!capacity) {
      alert("Please enter the bus capacity.");
      return;
    }

    const busData = {
      busName: busName.trim(),
      registrationNumber: registrationNumber.trim(),
      capacity: Number(capacity),
      busType: busType.trim(),
      isAc: isAc,
      pricePerTrip: pricePerTrip
        ? Number(pricePerTrip)
        : null,
      description: description.trim(),
    };

    const url = editingBusId
      ? `http://localhost:8080/api/buses/${editingBusId}`
      : "http://localhost:8080/api/buses";

    const method = editingBusId ? "PUT" : "POST";

    fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(busData),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            editingBusId
              ? "Failed to update bus"
              : "Failed to add bus"
          );
        }

        return response.json();
      })
      .then(() => {
        alert(
          editingBusId
            ? "Bus updated successfully!"
            : "Bus added successfully!"
        );

        resetForm();
        loadBuses();
      })
      .catch((error) => {
        console.error(error);
        alert(
          editingBusId
            ? "Could not update bus."
            : "Could not add bus."
        );
      });
  };

  /*
   * Delete bus
   */
  const deleteBus = (busId) => {
    if (!token) {
      alert("Please login as an agency.");
      return;
    }

    const confirmed = window.confirm(
      "Are you sure you want to delete this bus?"
    );

    if (!confirmed) {
      return;
    }

    fetch(`http://localhost:8080/api/buses/${busId}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to delete bus");
        }

        return response.text();
      })
      .then(() => {
        alert("Bus deleted successfully!");
        loadBuses();
      })
      .catch((error) => {
        console.error(error);
        alert("Could not delete bus.");
      });
  };

  return (
    <div className="my-buses-page">

      <div className="my-buses-header">

        <p className="tagline">
          AGENCY FLEET
        </p>

        <h1>
          My Buses
        </h1>

        <p>
          Manage the buses available for your agency's
          bookings and bids.
        </p>

      </div>

      <div className="my-buses-actions">

        <button
          className="add-bus-btn"
          onClick={() => {
            if (showForm) {
              resetForm();
            } else {
              setShowForm(true);
            }
          }}
        >
          {showForm ? "Cancel" : "+ Add Bus"}
        </button>

      </div>

      {showForm && (
        <div className="bus-form">

          <h2>
            {editingBusId
              ? "Edit Bus"
              : "Add New Bus"}
          </h2>

          <div className="bus-form-grid">

            <div className="form-field">
              <label>
                Bus Name
              </label>

              <input
                type="text"
                placeholder="Pune-Go Volvo 40 Seater"
                value={busName}
                onChange={(e) =>
                  setBusName(e.target.value)
                }
              />
            </div>

            <div className="form-field">
              <label>
                Registration Number
              </label>

              <input
                type="text"
                placeholder="MH12AB1234"
                value={registrationNumber}
                onChange={(e) =>
                  setRegistrationNumber(e.target.value)
                }
              />
            </div>

            <div className="form-field">
              <label>
                Capacity
              </label>

              <input
                type="number"
                min="1"
                placeholder="40"
                value={capacity}
                onChange={(e) =>
                  setCapacity(e.target.value)
                }
              />
            </div>

            <div className="form-field">
              <label>
                Bus Type
              </label>

              <input
                type="text"
                placeholder="Volvo / Sleeper / Mini Bus"
                value={busType}
                onChange={(e) =>
                  setBusType(e.target.value)
                }
              />
            </div>

            <div className="form-field">
              <label>
                Price Per Trip
              </label>

              <input
                type="number"
                min="0"
                placeholder="30000"
                value={pricePerTrip}
                onChange={(e) =>
                  setPricePerTrip(e.target.value)
                }
              />
            </div>

            <div className="form-field">

              <label>
                Air Conditioning
              </label>

              <select
                value={isAc ? "true" : "false"}
                onChange={(e) =>
                  setIsAc(e.target.value === "true")
                }
              >
                <option value="true">
                  AC
                </option>

                <option value="false">
                  Non-AC
                </option>
              </select>

            </div>

          </div>

          <div className="form-field">

            <label>
              Description
            </label>

            <textarea
              placeholder="Describe the bus, seating, amenities, luggage space, etc."
              value={description}
              onChange={(e) =>
                setDescription(e.target.value)
              }
            />

          </div>

          <button
            className="save-bus-btn"
            onClick={handleSubmit}
          >
            {editingBusId
              ? "Update Bus"
              : "Add Bus"}
          </button>

        </div>
      )}

      {loading && (
        <div className="buses-message">
          Loading your buses...
        </div>
      )}

      {message && (
        <div className="buses-message error">
          {message}
        </div>
      )}

      {!loading && !message && buses.length === 0 && (
        <div className="buses-message">
          You don't have any buses yet.
        </div>
      )}

      <div className="buses-grid">

        {buses.map((bus) => (

          <div
            className="bus-card"
            key={bus.busId}
          >

            <div className="bus-card-header">

              <div>

                <span className="bus-id">
                  Bus #{bus.busId}
                </span>

                <h2>
                  {bus.busName}
                </h2>

              </div>

              <span className="bus-ac-status">
                {bus.isAc ? "AC" : "NON-AC"}
              </span>

            </div>

            <div className="bus-info">

              <div>
                <span>
                  Registration
                </span>

                <strong>
                  {bus.registrationNumber}
                </strong>
              </div>

              <div>
                <span>
                  Capacity
                </span>

                <strong>
                  {bus.capacity} seats
                </strong>
              </div>

              <div>
                <span>
                  Bus Type
                </span>

                <strong>
                  {bus.busType || "Not specified"}
                </strong>
              </div>

              <div>
                <span>
                  Price / Trip
                </span>

                <strong>
                  {bus.pricePerTrip
                    ? `₹${Number(
                        bus.pricePerTrip
                      ).toLocaleString("en-IN")}`
                    : "Not specified"}
                </strong>
              </div>

            </div>

            <div className="bus-description">

              <span>
                Description
              </span>

              <p>
                {bus.description ||
                  "No description provided."}
              </p>

            </div>

            <div className="bus-card-actions">

              <button
                className="edit-bus-btn"
                onClick={() => startEditing(bus)}
              >
                Edit
              </button>

              <button
                className="delete-bus-btn"
                onClick={() => deleteBus(bus.busId)}
              >
                Delete
              </button>

            </div>

          </div>

        ))}

      </div>

    </div>
  );
}

export default MyBuses;
