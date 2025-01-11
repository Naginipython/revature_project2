import { useState, useEffect } from "react";
import useStore, { UserInfo } from "../../stores";
import { EnvelopeListCard } from "./EnvelopeListCard";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import { IconButton, Tooltip } from "@mui/material";
import { useNavigate } from "react-router-dom";

interface UserData {
  userId: number;
  username: string;
  email: string;
  role: string;
  firstName: string;
  lastName: string;
}

interface Envelope {
  envelopeId: number;
  user_id: number;
  envelopeDescription: string;
  balance: number;
  maxLimit: number;
  user: UserData;
}

export const EnvelopeList = () => {
  const user: UserInfo = useStore((state: any) => state.user);
  const [envelopeList, setEnvelopeList] = useState<Envelope[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    if (user?.token) {
      loadEnvelopeList();
    }
  }, [user?.token]);

  const loadEnvelopeList = async () => {
    try {
      console.log(user);
      console.log(user.userId);
      const response = await fetch(
        `http://localhost:8080/envelopes/user/${user.userId}`,
        {
          method: "GET",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${user.token}`,
          },
        }
      );
      if (!response.ok) {
        console.log("Error fetching envelopes");
      }
      const data = await response.json();
      setEnvelopeList(data); // Update state
      console.log(data);
    } catch (error) {
      console.error("Error fetching envelopes:", error);
    }
  };

  // Horribly inefficient way to render different tiers of envelopes, but whatever
  return (
    <div className="envelope-container">
      <div className="envelope-title-group">
        <p className="envelope-title">My Envelopes</p>
        <Tooltip title="Add new envelope" placement="bottom" arrow>
          <IconButton
            aria-label="add"
            size="large"
            onClick={() => navigate("/new_envelope")}
          >
            <AddCircleOutlineIcon fontSize="inherit" />
          </IconButton>
        </Tooltip>
      </div>
	  <hr className = "envelope-hr"/>
      {/* No envelopes, prompts to create one */}
      {envelopeList.length === 0 &&
        <div>
          <p className="envelope-subtitle">You have no envelopes...</p>
		  <br />
          <p className="envelope-subtitle-sub">Create a new envelope by clicking the + above!</p>
        </div>
      }
      <div className="envelope-row-group">
        {/* Shows if there are envelopes within the budget */}
        {envelopeList.some((env) => env.balance >= env.maxLimit / 2) &&
          <div>
            <p className="envelope-subtitle">Within Budget</p>
            <div className="envelope-row">
              {envelopeList.map((env) => {
                console.log(env.balance, env.maxLimit);
                // Within Budget - more than half balance remaining
                if (env.balance >= env.maxLimit / 2) {
                  return (
                    <EnvelopeListCard
                      key={env.envelopeId}
                      colorClass={"envelope-header-good"}
                      envelope={env}
                      onClick={() => console.log(env.envelopeId)}
                    />
                  );
                }
              })}
            </div>
          </div>
        }
        {/* Shows if there are envelopes nearly used */}
        {envelopeList.some((env) => env.balance > 0 && env.balance < env.maxLimit / 2) &&
          <div>
            <p className="envelope-subtitle">Nearly Used</p>
            <div className="envelope-row">
              {envelopeList.map((env) => {
                // Nearly Used - less than half, but still non zero balance
                if (env.balance > 0 && env.balance < env.maxLimit / 2) {
                  return (
                    <EnvelopeListCard
                      key={env.envelopeId}
                      colorClass={"envelope-header-warning"}
                      envelope={env}
                      onClick={() => console.log(env.envelopeId)}
                    />
                  );
                }
              })}
            </div>
          </div>
        }
        {/* Shows if there are envelopes over budget */}
        {envelopeList.some((env) => env.balance <= 0) &&
          <div>
            <p className="envelope-subtitle">Over Budget</p>
            <div className="envelope-row">
              {envelopeList.map((env) => {
                // Over Budget - balance is zero or negative
                if (env.balance <= 0) {
                  return (
                    <EnvelopeListCard
                      key={env.envelopeId}
                      colorClass={"envelope-header-danger"}
                      envelope={env}
                      onClick={() => console.log(env.envelopeId)}
                    />
                  );
                }
              })}
            </div>
          </div>
        }
      </div>
    </div>
  );
};
