import React from 'react';

const ScoreCard = ({ score }) => {
  const getScoreInfo = (s) => {
    if (s >= 90) return { label: 'Excellent', color: 'var(--text-primary)' };
    if (s >= 70) return { label: 'Needs Improvement', color: 'var(--text-secondary)' };
    return { label: 'Poor', color: 'var(--accent-primary)' }; // Red for poor
  };

  const info = getScoreInfo(score);

  return (
    <div className="news-card sharp-corners" style={{
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'space-between',
      height: '100%',
      position: 'relative'
    }}>
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'flex-start',
        borderBottom: '4px solid var(--border-color)',
        paddingBottom: '1rem',
        marginBottom: '1rem'
      }}>
        <h3 className="font-sans" style={{ fontSize: '0.85rem', fontWeight: 700, letterSpacing: '0.1em', textTransform: 'uppercase', margin: 0 }}>
          Accessibility Rating
        </h3>
        <span className="font-mono" style={{ fontSize: '0.75rem', fontWeight: 700, letterSpacing: '0.1em' }}>FIG. 1</span>
      </div>
      
      <div style={{ 
        flex: 1, 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center',
        padding: '2rem 0' 
      }}>
        <div style={{
          fontSize: '8rem',
          fontWeight: '900',
          fontFamily: 'Playfair Display, serif',
          lineHeight: '0.8',
          color: info.color,
          letterSpacing: '-0.05em'
        }}>
          {score}
        </div>
        <div style={{
          fontSize: '2rem',
          fontWeight: '700',
          fontFamily: 'Playfair Display, serif',
          color: info.color,
          alignSelf: 'flex-start',
          marginTop: '0.5rem'
        }}>
          /100
        </div>
      </div>
      
      <div style={{
        paddingTop: '1rem',
        borderTop: '1px solid var(--border-color)',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <span className="font-body" style={{ fontStyle: 'italic', color: 'var(--text-secondary)' }}>Overall Assessment</span>
        <span className="news-badge sharp-corners" style={{
          backgroundColor: info.color === 'var(--accent-primary)' ? 'var(--accent-primary)' : 'var(--text-primary)',
          color: 'var(--bg-primary)'
        }}>
          {info.label}
        </span>
      </div>
    </div>
  );
};

export default ScoreCard;
