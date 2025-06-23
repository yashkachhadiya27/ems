import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'minuteHour',
  standalone: true,
})
export class MinuteHourPipe implements PipeTransform {
  transform(value: number): string {
    const hours = Math.floor(value / 3600);
    const minutes = Math.floor((value % 3600) / 60);
    return `${hours}h ${minutes}m`;
  }
}
