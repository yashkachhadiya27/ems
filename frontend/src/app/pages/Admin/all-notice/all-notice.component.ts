import { Component, inject, OnInit } from '@angular/core';
import { NoticeService } from '../../../core/Services/notice.service';
import { Notice } from '../../../core/Interfaces/NoticeModel';
import { Router } from '@angular/router';

@Component({
  selector: 'app-all-notice',
  standalone: true,
  imports: [],
  templateUrl: './all-notice.component.html',
  styleUrl: './all-notice.component.css',
})
export class AllNoticeComponent implements OnInit {
  private noticeService = inject(NoticeService);
  private route = inject(Router);
  notices: Notice[] = [];
  ngOnInit(): void {
    this.noticeService.getActiveNotices().subscribe({
      next: (res) => (this.notices = res),
    });
  }
  showNotice(index: number) {
    this.route.navigate(['/adminEmployee/notice'], {
      queryParams: {
        data: JSON.stringify(this.notices[index]),
      },
    });
  }
}
