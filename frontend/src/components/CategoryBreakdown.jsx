import React from 'react';
import { Layout, Image, Link, Type } from 'lucide-react';

const CategoryBreakdown = ({ breakdown }) => {
  if (!breakdown) return null;

  const categories = [
    { key: 'STRUCTURE', label: 'Structure', icon: Layout },
    { key: 'IMAGES', label: 'Images & Media', icon: Image },
    { key: 'LINKS', label: 'Links & Nav', icon: Link },
    { key: 'FORMS', label: 'Forms & Inputs', icon: Type },
  ];

  return (
    <div className="news-card sharp-corners" style={{
      height: '100%',
      display: 'flex',
      flexDirection: 'column',
      padding: 0 // Remove default padding for grid
    }}>
      <div style={{
        padding: '1.5rem',
        borderBottom: '4px solid var(--border-color)',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'baseline'
      }}>
        <h3 className="font-sans" style={{ fontSize: '0.85rem', fontWeight: 700, letterSpacing: '0.1em', textTransform: 'uppercase', margin: 0 }}>
          Category Breakdown
        </h3>
        <span className="font-mono" style={{ fontSize: '0.75rem', fontWeight: 700, letterSpacing: '0.1em' }}>FIG. 2</span>
      </div>
      
      <div style={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
        {categories.map((cat, index) => {
          const scores = breakdown.categoryScores || breakdown;
          const score = scores[cat.key] !== undefined ? scores[cat.key] : 100;
          const Icon = cat.icon;
          const isLast = index === categories.length - 1;
          
          return (
            <div key={cat.key} style={{ 
              display: 'flex', 
              alignItems: 'center',
              borderBottom: isLast ? 'none' : '1px solid var(--border-color)',
              padding: '1rem 1.5rem',
              flex: 1
            }}>
              <div style={{ 
                border: '1px solid var(--border-color)', 
                width: '40px', 
                height: '40px', 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center',
                marginRight: '1rem',
                flexShrink: 0
              }}>
                <Icon size={20} strokeWidth={1.5} />
              </div>
              
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: '0.25rem' }}>
                  <span className="font-serif" style={{ fontSize: '1.25rem', fontWeight: 600 }}>{cat.label}</span>
                  <span className="font-mono" style={{ fontSize: '1rem', fontWeight: 700 }}>{score}%</span>
                </div>
                
                <div style={{ 
                  height: '4px', 
                  backgroundColor: 'var(--neutral-200)',
                  width: '100%'
                }}>
                  <div style={{ 
                    height: '100%', 
                    backgroundColor: 'var(--text-primary)',
                    width: `${score}%`,
                    transition: 'width 0.5s ease-out'
                  }} />
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default CategoryBreakdown;
