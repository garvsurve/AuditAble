import React from 'react';
import { motion } from 'framer-motion';
import { Layout, Image, Link, Type } from 'lucide-react';

const CategoryBreakdown = ({ breakdown }) => {
  // If breakdown isn't provided directly, handle gracefully
  if (!breakdown) return null;

  const categories = [
    { key: 'STRUCTURE', label: 'Structure', icon: Layout, color: '#6366f1' },
    { key: 'IMAGES', label: 'Images & Media', icon: Image, color: '#f59e0b' },
    { key: 'LINKS', label: 'Links & Navigation', icon: Link, color: '#10b981' },
    { key: 'FORMS', label: 'Forms & Inputs', icon: Type, color: '#ec4899' },
  ];

  return (
    <div className="glass-effect" style={{
      padding: '2rem',
      borderRadius: 'var(--border-radius-lg)',
      height: '100%',
      display: 'flex',
      flexDirection: 'column'
    }}>
      <h3 style={{ marginBottom: '1.5rem', color: 'var(--text-secondary)' }}>Category Breakdown</h3>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem', flex: 1, justifyContent: 'center' }}>
        {categories.map((cat, index) => {
          const score = breakdown[cat.key] !== undefined ? breakdown[cat.key] : 100;
          const Icon = cat.icon;
          
          return (
            <div key={cat.key}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--text-primary)' }}>
                  <Icon size={16} color={cat.color} />
                  <span style={{ fontSize: '0.95rem', fontWeight: '500' }}>{cat.label}</span>
                </div>
                <span style={{ fontWeight: '600', color: 'var(--text-primary)' }}>{score}%</span>
              </div>
              
              <div style={{ 
                height: '8px', 
                backgroundColor: 'rgba(255,255,255,0.05)', 
                borderRadius: '4px',
                overflow: 'hidden'
              }}>
                <motion.div 
                  initial={{ width: 0 }}
                  animate={{ width: `${score}%` }}
                  transition={{ duration: 1, delay: 0.2 + (index * 0.1) }}
                  style={{ 
                    height: '100%', 
                    backgroundColor: cat.color,
                    borderRadius: '4px',
                    boxShadow: `0 0 10px ${cat.color}80` 
                  }}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default CategoryBreakdown;
