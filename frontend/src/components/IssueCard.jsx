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
    }}>
      {/* ── Header row: icon + title + badges ── */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start' }}>
          <div style={{
            backgroundColor: severityStyle.bg,
            padding: '8px',
            borderRadius: '8px',
            color: severityStyle.color,
            flexShrink: 0,
          }}>
            <Icon size={20} />
          </div>
          <div>
            <h4 style={{ color: 'var(--text-primary)', fontSize: '1.1rem', marginBottom: '6px' }}>
              {issue.type || 'Accessibility Issue'}
            </h4>

            {/* ── Badge row: rule + severity ── */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}>
              {/* Rule badge */}
              {issue.rule && (
                <span style={{
                  display: 'inline-block',
                  fontSize: '0.72rem',
                  fontWeight: '600',
                  padding: '2px 8px',
                  backgroundColor: 'rgba(255,255,255,0.06)',
                  color: 'var(--text-secondary)',
                  border: '1px solid rgba(255,255,255,0.12)',
                  borderRadius: '4px',
                  letterSpacing: '0.04em',
                  fontFamily: 'monospace',
                }}>
                  {issue.rule}
                </span>
              )}

              {/* WCAG badge and link */}
              {issue.wcag && (
                <a
                  href={`https://www.w3.org/WAI/WCAG21/quickref/?showtechniques=141#${issue.wcag.split(',')[0].trim().replace(/\./g, '-')}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: '4px',
                    fontSize: '0.75rem',
                    fontWeight: '700',
                    padding: '2px 10px',
                    backgroundColor: 'rgba(99, 102, 241, 0.15)',
                    color: 'var(--accent-primary)',
                    border: '1px solid rgba(99, 102, 241, 0.3)',
                    borderRadius: '4px',
                    textDecoration: 'none',
                    letterSpacing: '0.05em',
                  }}
                  title="View WCAG Reference"
                >
                  WCAG {issue.wcag}
                </a>
              )}

              {/* Severity badge */}
              <span style={{
                display: 'inline-block',
                fontSize: '0.75rem',
                fontWeight: '600',
                padding: '2px 8px',
                backgroundColor: severityStyle.bg,
                color: severityStyle.color,
                borderRadius: '4px',
                letterSpacing: '0.05em',
              }}>
                {issue.severity?.toUpperCase() || 'LOW'}
              </span>


            </div>
          </div>
        </div>
      </div>

      {/* ── Message ── */}
      <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', lineHeight: '1.6', marginBottom: '1.25rem' }}>
        {issue.message}
      </p>

      {/* ── AI / Rule Suggestion ── */}
      {issue.suggestion && (
        <div style={{
          backgroundColor: 'rgba(99, 102, 241, 0.05)',
          border: '1px solid var(--accent-glow)',
          padding: '1rem',
          borderRadius: 'var(--border-radius-sm)',
          display: 'flex',
          gap: '12px',
          alignItems: 'flex-start',
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
