import { Component, inject, OnInit } from '@angular/core';
import { NoticeService } from '../../../core/Services/notice.service';
import { Notice } from '../../../core/Interfaces/NoticeModel';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-notice',
  standalone: true,
  imports: [],
  templateUrl: './notice.component.html',
  styleUrl: './notice.component.css',
})
export class NoticeComponent implements OnInit {
  private route = inject(ActivatedRoute);
  notice: Notice = { title: '', content: '', deadline: '' };
  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.notice = JSON.parse(params['data']);
    });
  }
}
