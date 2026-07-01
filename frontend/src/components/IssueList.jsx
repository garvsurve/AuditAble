import React, { useState } from 'react';
import { Search, Filter } from 'lucide-react';
import IssueCard from './IssueCard';

const IssueList = ({ issues = [] }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [severityFilter, setSeverityFilter] = useState('ALL');

  const filteredIssues = issues.filter(issue => {
    const matchesSearch =
      (issue.message || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
      (issue.type || '').toLowerCase().includes(searchTerm.toLowerCase());

    const matchesSeverity =
      severityFilter === 'ALL' ||
      (issue.severity || '').toUpperCase() === severityFilter;

    return matchesSearch && matchesSeverity;
  });

  return (
    <div style={{ marginTop: '4rem', borderTop: '4px solid var(--border-color)' }}>
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '1.5rem 0',
        borderBottom: '2px solid var(--border-color)',
        flexWrap: 'wrap',
        gap: '1rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: '12px' }}>
          <h2 className="font-serif" style={{ fontSize: '2rem', margin: 0, textTransform: 'none' }}>Detailed Findings</h2>
          <span className="font-mono" style={{
            fontSize: '0.85rem',
            fontWeight: 700,
            border: '1px solid var(--border-color)',
            padding: '2px 8px',
          }}>
            {filteredIssues.length} ISSUES
          </span>
        </div>

        <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
          <div style={{ position: 'relative' }}>
            <Search size={16} strokeWidth={1.5} style={{ position: 'absolute', left: '0', bottom: '10px' }} />
            <input
              type="text"
              placeholder="Search..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="news-input"
              style={{ paddingLeft: '24px', width: '200px', fontSize: '0.9rem' }}
            />
          </div>

          <div style={{ position: 'relative' }}>
            <Filter size={16} strokeWidth={1.5} style={{ position: 'absolute', left: '0', bottom: '10px' }} />
            <select
              value={severityFilter}
              onChange={(e) => setSeverityFilter(e.target.value)}
              className="news-input"
              style={{ paddingLeft: '24px', appearance: 'none', cursor: 'pointer', fontSize: '0.9rem' }}
            >
              <option value="ALL">All Severities</option>
              <option value="HIGH">High Severity</option>
              <option value="MEDIUM">Medium Severity</option>
              <option value="LOW">Low Severity</option>
            </select>
          </div>
        </div>
      </div>

      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
        borderBottom: '1px solid var(--border-color)',
        borderLeft: '1px solid var(--border-color)'
      }}>
        {filteredIssues.length > 0 ? (
          filteredIssues.map((issue, idx) => (
            <IssueCard key={idx} issue={issue} />
          ))
        ) : (
          <div style={{ 
            gridColumn: '1 / -1',
            padding: '4rem 2rem', 
            textAlign: 'center',
            borderRight: '1px solid var(--border-color)',
            borderBottom: '1px solid var(--border-color)'
          }}>
            <p className="font-serif" style={{ fontSize: '1.5rem', color: 'var(--text-secondary)' }}>No issues found matching the criteria.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default IssueList;
