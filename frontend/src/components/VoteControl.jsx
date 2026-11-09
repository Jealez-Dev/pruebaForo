import { useEffect, useState } from 'react';
import { getVoteSummary, saveVote } from '../api';

const initialSummary = { upvotes: 0, downvotes: 0, estado: 'NONE' };

export default function VoteControl({ targetType, targetId, userId = 1, compact = false }) {
  const [summary, setSummary] = useState(initialSummary);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    let active = true;
    getVoteSummary(targetType, targetId, userId)
      .then((value) => active && setSummary(value))
      .catch((requestError) => active && setError(requestError.message));
    return () => { active = false; };
  }, [targetType, targetId, userId]);

  async function vote(state) {
    if (saving) return;
    const previous = summary;
    const nextState = previous.estado === state ? 'NONE' : state;
    setSummary({
      ...previous,
      estado: nextState,
      upvotes: previous.upvotes + (nextState === 'UPVOTE' ? 1 : previous.estado === 'UPVOTE' ? -1 : 0),
      downvotes: previous.downvotes + (nextState === 'DOWNVOTE' ? 1 : previous.estado === 'DOWNVOTE' ? -1 : 0)
    });
    setSaving(true);
    setError('');
    try {
      setSummary(await saveVote(targetType, targetId, userId, nextState));
    } catch (requestError) {
      setSummary(previous);
      setError(requestError.message);
    } finally {
      setSaving(false);
    }
  }

  const score = summary.upvotes - summary.downvotes;
  return (
    <div className={`vote-control ${compact ? 'vote-control-compact' : ''}`} aria-label="Control de votación">
      <button className={summary.estado === 'UPVOTE' ? 'vote-active-up' : ''} type="button" onClick={() => vote('UPVOTE')} disabled={saving} aria-label="Votar positivo">▲</button>
      <span>{score}</span>
      <button className={summary.estado === 'DOWNVOTE' ? 'vote-active-down' : ''} type="button" onClick={() => vote('DOWNVOTE')} disabled={saving} aria-label="Votar negativo">▼</button>
      {error && <span className="vote-error" role="alert">{error}</span>}
    </div>
  );
}
