/**
 * 消息提示组件
 */
import { useEffect, useState } from 'react';

type MessageType = 'success' | 'error' | 'warning' | 'info';

interface MessageProps {
  type: MessageType;
  content: string;
  duration?: number;
  onClose?: () => void;
}

const typeStyles: Record<MessageType, string> = {
  success: 'bg-green-50 text-green-800 border-green-200',
  error: 'bg-red-50 text-red-800 border-red-200',
  warning: 'bg-yellow-50 text-yellow-800 border-yellow-200',
  info: 'bg-blue-50 text-blue-800 border-blue-200',
};

const icons: Record<MessageType, string> = {
  success: '✓',
  error: '✕',
  warning: '⚠',
  info: 'ℹ',
};

export function Message({ type, content, duration = 3000, onClose }: MessageProps) {
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setVisible(false);
      onClose?.();
    }, duration);

    return () => clearTimeout(timer);
  }, [duration, onClose]);

  if (!visible) return null;

  return (
    <div
      className={`
        fixed top-4 left-1/2 -translate-x-1/2 z-50
        px-4 py-3 rounded-md border shadow-lg
        flex items-center gap-2
        animate-fade-in
        ${typeStyles[type]}
      `}
    >
      <span className="text-lg">{icons[type]}</span>
      <span>{content}</span>
    </div>
  );
}

// 全局消息管理
let messageContainer: HTMLDivElement | null = null;

function createContainer() {
  if (!messageContainer) {
    messageContainer = document.createElement('div');
    messageContainer.id = 'message-container';
    document.body.appendChild(messageContainer);
  }
  return messageContainer;
}

export function showMessage(type: MessageType, content: string, duration?: number) {
  const container = createContainer();
  const messageDiv = document.createElement('div');

  const typeStyle = typeStyles[type];
  const icon = icons[type];

  messageDiv.className = `
    px-4 py-3 rounded-md border shadow-lg
    flex items-center gap-2 mb-2
    animate-fade-in
    ${typeStyle}
  `;
  messageDiv.innerHTML = `
    <span class="text-lg">${icon}</span>
    <span>${content}</span>
  `;

  container.appendChild(messageDiv);

  setTimeout(() => {
    messageDiv.style.opacity = '0';
    messageDiv.style.transform = 'translateY(-10px)';
    messageDiv.style.transition = 'all 0.3s';
    setTimeout(() => messageDiv.remove(), 300);
  }, duration || 3000);
}

export const message = {
  success: (content: string, duration?: number) => showMessage('success', content, duration),
  error: (content: string, duration?: number) => showMessage('error', content, duration),
  warning: (content: string, duration?: number) => showMessage('warning', content, duration),
  info: (content: string, duration?: number) => showMessage('info', content, duration),
};
