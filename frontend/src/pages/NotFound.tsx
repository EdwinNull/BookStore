/**
 * 404 页面
 */
import { Link } from 'react-router-dom';
import { Button } from '@/components/common';

export function NotFoundPage() {
  return (
    <div className="min-h-[60vh] flex items-center justify-center">
      <div className="text-center">
        <h1 className="text-9xl font-bold text-gray-200">404</h1>
        <p className="text-2xl font-medium text-gray-600 mt-4">页面不存在</p>
        <p className="text-gray-500 mt-2">您访问的页面已经不存在了</p>
        <Link to="/">
          <Button className="mt-6">返回首页</Button>
        </Link>
      </div>
    </div>
  );
}
