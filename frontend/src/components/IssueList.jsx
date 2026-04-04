import React, { useState } from 'react';
import { Search, Filter } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import IssueCard from './IssueCard';

const IssueList = ({ issues = [] }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [severityFilter, setSeverityFilter] = useState('ALL');

  const filteredIssues = issues.filter(issue => {
    // Search match
    const matchesSearch =
      (issue.message || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
      (issue.type || '').toLowerCase().includes(searchTerm.toLowerCase());

    // Severity match
    const matchesSeverity =
      severityFilter === 'ALL' ||
      (issue.severity || '').toUpperCase() === severityFilter;

    return matchesSearch && matchesSeverity;
  });

  return (
    <div className="glass-effect" style={{
      padding: '2rem',
      borderRadius: 'var(--border-radius-lg)',
      marginTop: '2rem'
    }}>
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '1.5rem',
        flexWrap: 'wrap',
        gap: '1rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <h3 style={{ color: 'var(--text-primary)', fontSize: '1.25rem' }}>Identified Issues</h3>
          <span style={{
            backgroundColor: 'rgba(255,255,255,0.1)',
            padding: '2px 10px',
            borderRadius: '99px',
            fontSize: '0.85rem',
            color: 'var(--text-secondary)',
            fontWeight: '600'
          }}>
            {filteredIssues.length}
          </span>
        </div>

        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
          {/* Search box */}
          <div style={{ position: 'relative' }}>
            <Search size={16} color="var(--text-tertiary)" style={{ position: 'absolute', left: '12px', top: '10px' }} />
            <input
              type="text"
              placeholder="Search issues..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{
                backgroundColor: 'rgba(0,0,0,0.2)',
                border: '1px solid var(--border-color)',
                borderRadius: 'var(--border-radius-sm)',
                padding: '8px 12px 8px 36px',
                color: 'var(--text-primary)',
                fontSize: '0.9rem',
                outline: 'none',
                width: '200px'
              }}
            />
          </div>

          {/* Filter dropdown */}
          <div style={{ position: 'relative' }}>
            <Filter size={16} color="var(--text-tertiary)" style={{ position: 'absolute', left: '12px', top: '10px' }} />
            <select
              value={severityFilter}
              onChange={(e) => setSeverityFilter(e.target.value)}
              style={{
                backgroundColor: 'rgba(0,0,0,0.2)',
                border: '1px solid var(--border-color)',
                borderRadius: 'var(--border-radius-sm)',
                padding: '8px 12px 8px 36px',
                color: 'var(--text-primary)',
                fontSize: '0.9rem',
                outline: 'none',
                appearance: 'none',
                cursor: 'pointer'
              }}
            >
              <option value="ALL">All Severities</option>
              <option value="HIGH">High Severity</option>
              <option value="MEDIUM">Medium Severity</option>
              <option value="LOW">Low Severity</option>
            </select>
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column' }}>
        <AnimatePresence>
          {filteredIssues.length > 0 ? (
            filteredIssues.map((issue, idx) => (
              <motion.div
                key={idx}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, scale: 0.95 }}
                transition={{ duration: 0.2 }}
              >
                <IssueCard issue={issue} />
              </motion.div>
            ))
          ) : (
            <motion.div
              initial={{ opacity: 0 }} animate={{ opacity: 1 }}
              style={{ textAlign: 'center', padding: '3rem 1rem', color: 'var(--text-tertiary)' }}
            >
              <p>No issues found matching your criteria.</p>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
};

export default IssueList;
