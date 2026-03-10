/**
 * 分页组件
 */
interface PaginationProps {
  current: number;
  total: number;
  pageSize: number;
  onChange: (page: number) => void;
}

export function Pagination({ current, total, pageSize, onChange }: PaginationProps) {
  const totalPages = Math.ceil(total / pageSize);

  if (totalPages <= 1) return null;

  const pages: (number | string)[] = [];
  const showEllipsisStart = current > 3;
  const showEllipsisEnd = current < totalPages - 2;

  // 生成页码
  if (totalPages <= 7) {
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i);
    }
  } else {
    pages.push(1);
    if (showEllipsisStart) {
      pages.push('...');
    }
    for (let i = Math.max(2, current - 1); i <= Math.min(totalPages - 1, current + 1); i++) {
      if (!pages.includes(i)) {
        pages.push(i);
      }
    }
    if (showEllipsisEnd) {
      pages.push('...');
    }
    if (!pages.includes(totalPages)) {
      pages.push(totalPages);
    }
  }

  return (
    <div className="flex items-center justify-center gap-1">
      {/* 上一页 */}
      <button
        onClick={() => onChange(current - 1)}
        disabled={current === 1}
        className="px-3 py-1 rounded border border-gray-300 text-sm
          disabled:opacity-50 disabled:cursor-not-allowed
          hover:bg-gray-100 transition-colors"
      >
        上一页
      </button>

      {/* 页码 */}
      {pages.map((page, index) => (
        <button
          key={index}
          onClick={() => typeof page === 'number' && onChange(page)}
          disabled={page === '...'}
          className={`
            w-8 h-8 rounded text-sm
            ${page === current
              ? 'bg-blue-500 text-white'
              : page === '...'
                ? 'cursor-default'
                : 'border border-gray-300 hover:bg-gray-100'
            }
            transition-colors
          `}
        >
          {page}
        </button>
      ))}

      {/* 下一页 */}
      <button
        onClick={() => onChange(current + 1)}
        disabled={current === totalPages}
        className="px-3 py-1 rounded border border-gray-300 text-sm
          disabled:opacity-50 disabled:cursor-not-allowed
          hover:bg-gray-100 transition-colors"
      >
        下一页
      </button>
    </div>
  );
}
