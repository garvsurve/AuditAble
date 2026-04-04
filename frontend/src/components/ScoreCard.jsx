import React, { useMemo } from 'react';
import { motion } from 'framer-motion';

const ScoreCard = ({ score }) => {
  const getScoreInfo = (s) => {
    if (s >= 90) return { color: 'var(--success-color)', bg: 'var(--success-bg)', label: 'Excellent' };
    if (s >= 70) return { color: 'var(--warning-color)', bg: 'var(--warning-bg)', label: 'Needs Improvement' };
    return { color: 'var(--danger-color)', bg: 'var(--danger-bg)', label: 'Poor' };
  };

  const info = getScoreInfo(score);
  
  // Circumference for circular progress
  const radius = 60;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (score / 100) * circumference;

  return (
    <div className="glass-effect" style={{
      padding: '2rem',
      borderRadius: 'var(--border-radius-lg)',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      height: '100%'
    }}>
      <h3 style={{ marginBottom: '1.5rem', color: 'var(--text-secondary)' }}>Overall Accessibility</h3>
      
      <div style={{ position: 'relative', width: '160px', height: '160px' }}>
        {/* Background circle */}
        <svg fill="transparent" width="160" height="160" viewBox="0 0 160 160">
          <circle
            cx="80"
            cy="80"
            r={radius}
            stroke="var(--border-color)"
            strokeWidth="12"
          />
        </svg>

        {/* Animated progress circle */}
        <svg 
          fill="transparent" 
          width="160" 
          height="160" 
          viewBox="0 0 160 160"
          style={{ position: 'absolute', top: 0, left: 0, transform: 'rotate(-90deg)' }}
        >
          <motion.circle
            cx="80"
            cy="80"
            r={radius}
            stroke={info.color}
            strokeWidth="12"
            strokeLinecap="round"
            strokeDasharray={circumference}
            initial={{ strokeDashoffset: circumference }}
            animate={{ strokeDashoffset }}
            transition={{ duration: 1.5, ease: "easeOut" }}
          />
        </svg>

        {/* Score text inside circle */}
        <div style={{
          position: 'absolute',
          top: '0',
          left: '0',
          width: '100%',
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center'
        }}>
          <motion.span 
            initial={{ opacity: 0, scale: 0.5 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: 0.5 }}
            style={{ fontSize: '3rem', fontWeight: '800', color: 'var(--text-primary)', lineHeight: '1' }}
          >
            {score}
          </motion.span>
        </div>
      </div>
      
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 1 }}
        style={{
          marginTop: '1.5rem',
          padding: '6px 16px',
          backgroundColor: info.bg,
          color: info.color,
          borderRadius: '999px',
          fontWeight: '600',
          fontSize: '0.9rem'
        }}
      >
        {info.label}
      </motion.div>
    </div>
  );
};

export default ScoreCard;
