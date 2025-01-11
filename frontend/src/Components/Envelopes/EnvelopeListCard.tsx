import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import { useNavigate } from "react-router-dom";

// lets go dude snake case and camel case in the same codebase???? gamign

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

interface IProps {
  colorClass: string;
  envelope: Envelope;
  onClick: () => void;
}

export const EnvelopeListCard: React.FC<IProps> = ({
  colorClass,
  envelope,
}: IProps) => {
  // Horribly inefficient way to render different tiers of envelopes, but whatever

  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/envelope/${envelope.envelopeId}`);
  };
  return (
    <Card
      sx={{
        minWidth: 275,
        minHeight: 275,
        boxShadow: 1,
        borderRadius: 2,
        margin: 1,
      }}
      variant="outlined"
      onClick={handleClick}
    >
      <div className={colorClass}></div>
      <CardContent>
        <Typography variant="h5" component="div" sx={{ fontSize: 25 }}>
          {envelope.envelopeDescription}
        </Typography>
        <Typography variant="h1" component="div" sx={{ fontSize: 25 }}>
          {"$" + envelope.balance + "/$" + envelope.maxLimit}
        </Typography>
      </CardContent>
    </Card>
  );
};
