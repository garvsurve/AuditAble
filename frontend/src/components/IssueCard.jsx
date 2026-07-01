import React from 'react';
import { AlertCircle, AlertTriangle, Info, Sparkles } from 'lucide-react';

const IssueCard = ({ issue }) => {
  const getSeverityStyles = (severity) => {
    switch (severity?.toUpperCase()) {
      case 'HIGH':
        return { color: 'var(--accent-primary)', icon: AlertCircle, label: 'HIGH' };
      case 'MEDIUM':
        return { color: 'var(--text-primary)', icon: AlertTriangle, label: 'MED' };
      case 'LOW':
      default:
        return { color: 'var(--text-secondary)', icon: Info, label: 'LOW' };
    }
  };

  const severityStyle = getSeverityStyles(issue.severity);
  const Icon = severityStyle.icon;

  return (
    <div className="hard-shadow-hover" style={{
      backgroundColor: 'var(--bg-primary)',
      borderRight: '1px solid var(--border-color)',
      borderBottom: '1px solid var(--border-color)',
      padding: '1.5rem',
      display: 'flex',
      flexDirection: 'column'
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start' }}>
          <div style={{
            border: '1px solid var(--border-color)',
            padding: '8px',
            color: severityStyle.color,
            flexShrink: 0,
            backgroundColor: 'var(--bg-primary)'
          }}>
            <Icon size={20} strokeWidth={1.5} />
          </div>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap', marginBottom: '8px' }}>
              <span className="news-badge" style={{
                backgroundColor: severityStyle.color === 'var(--accent-primary)' ? 'var(--accent-primary)' : 'var(--text-primary)',
                color: 'var(--bg-primary)'
              }}>
                {severityStyle.label}
              </span>
              
              {issue.rule && (
                <span className="font-mono" style={{
                  fontSize: '0.72rem',
                  fontWeight: '600',
                  color: 'var(--text-secondary)',
                  letterSpacing: '0.04em',
                }}>
                  RULE: {issue.rule}
                </span>
              )}
            </div>

            <h4 className="font-serif" style={{ color: 'var(--text-primary)', fontSize: '1.25rem', lineHeight: 1.3, fontWeight: 700, margin: 0 }}>
              {issue.type || 'Accessibility Issue'}
            </h4>
          </div>
        </div>
      </div>

      <p className="font-body" style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', lineHeight: '1.6', marginBottom: '1.5rem', flex: 1 }}>
        {issue.message}
      </p>

      {issue.wcag && (
        <div style={{ marginBottom: '1rem' }}>
          <a
            href={`https://www.w3.org/WAI/WCAG21/quickref/?showtechniques=141#${issue.wcag.split(',')[0].trim().replace(/\./g, '-')}`}
            target="_blank"
            rel="noopener noreferrer"
            className="font-mono hover:bg-[#111111] hover:text-[#F9F9F7]"
            style={{
              display: 'inline-flex',
              fontSize: '0.75rem',
              fontWeight: '700',
              padding: '4px 8px',
              color: 'var(--text-primary)',
              border: '1px solid var(--border-color)',
              textDecoration: 'none',
              transition: 'all 0.2s',
            }}
          >
            WCAG REF: {issue.wcag}
          </a>
        </div>
      )}

      {issue.suggestion && (
        <div style={{
          backgroundColor: 'var(--neutral-100)',
          borderTop: '2px solid var(--border-color)',
          padding: '1rem',
          display: 'flex',
          gap: '12px',
          alignItems: 'flex-start',
        }}>
          <Sparkles size={16} strokeWidth={1.5} color="var(--text-primary)" style={{ marginTop: '2px', flexShrink: 0 }} />
          <div>
            <h5 className="font-sans" style={{ color: 'var(--text-primary)', fontSize: '0.75rem', fontWeight: 700, margin: '0 0 4px 0', textTransform: 'uppercase', letterSpacing: '0.1em' }}>
              Editorial Suggestion
            </h5>
            <p className="font-body" style={{ color: 'var(--text-primary)', fontSize: '0.9rem', lineHeight: '1.5', margin: 0 }}>
              {issue.suggestion}
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default IssueCard;
