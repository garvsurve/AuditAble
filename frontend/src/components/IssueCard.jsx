import React from 'react';
import { AlertCircle, AlertTriangle, Info, Sparkles } from 'lucide-react';

const IssueCard = ({ issue }) => {
  const getSeverityStyles = (severity) => {
    switch (severity?.toUpperCase()) {
      case 'HIGH':
        return { color: 'var(--danger-color)', bg: 'var(--danger-bg)', icon: AlertCircle };
      case 'MEDIUM':
        return { color: 'var(--warning-color)', bg: 'var(--warning-bg)', icon: AlertTriangle };
      case 'LOW':
      default:
        return { color: '#3b82f6', bg: 'rgba(59, 130, 246, 0.1)', icon: Info };
    }
  };

  const severityStyle = getSeverityStyles(issue.severity);
  const Icon = severityStyle.icon;

  return (
    <div style={{
      backgroundColor: 'rgba(20, 20, 22, 0.4)',
      border: `1px solid ${severityStyle.bg}`,
      borderRadius: 'var(--border-radius-md)',
      padding: '1.5rem',
      marginBottom: '1rem',
      transition: 'all 0.2s',
      ':hover': {
        backgroundColor: 'rgba(20, 20, 22, 0.6)'
      }
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start' }}>
          <div style={{
            backgroundColor: severityStyle.bg,
            padding: '8px',
            borderRadius: '8px',
            color: severityStyle.color
          }}>
            <Icon size={20} />
          </div>
          <div>
            <h4 style={{ color: 'var(--text-primary)', fontSize: '1.1rem', marginBottom: '4px' }}>
              {issue.type || 'Accessibility Issue'}
            </h4>
            <span style={{
              display: 'inline-block',
              fontSize: '0.75rem',
              fontWeight: '600',
              padding: '2px 8px',
              backgroundColor: severityStyle.bg,
              color: severityStyle.color,
              borderRadius: '4px',
              letterSpacing: '0.05em'
            }}>
              {issue.severity?.toUpperCase() || 'LOW'}
            </span>
          </div>
        </div>
      </div>

      <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', lineHeight: '1.6', marginBottom: '1.25rem' }}>
        {issue.message}
      </p>

      {issue.suggestion && (
        <div style={{
          backgroundColor: 'rgba(99, 102, 241, 0.05)',
          border: '1px solid var(--accent-glow)',
          padding: '1rem',
          borderRadius: 'var(--border-radius-sm)',
          display: 'flex',
          gap: '12px',
          alignItems: 'flex-start'
        }}>
          <Sparkles size={18} color="var(--accent-primary)" style={{ marginTop: '2px', flexShrink: 0 }} />
          <div>
            <h5 style={{ color: 'var(--accent-primary)', fontSize: '0.85rem', marginBottom: '4px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>AI Suggestion</h5>
            <p style={{ color: 'var(--text-primary)', fontSize: '0.9rem', lineHeight: '1.5' }}>
              {issue.suggestion}
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default IssueCard;
