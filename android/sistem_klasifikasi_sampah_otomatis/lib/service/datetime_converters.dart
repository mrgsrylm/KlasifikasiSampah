import 'package:intl/intl.dart';

class DatetimeConverters {
  static final DatetimeConverters constants = DatetimeConverters._();
  factory DatetimeConverters() => constants;
  DatetimeConverters._();
  String convertToAsiaJakarta(int unixTimestampMillis) {

    DateTime dateTimeMillis = DateTime.fromMillisecondsSinceEpoch(unixTimestampMillis * 1000);
    DateTime jakartaTime = dateTimeMillis.toUtc().add(const Duration(hours: 7));
    String result = DateFormat.EEEE().addPattern(', dd-MM-yyyy').add_Hms().format(jakartaTime);
    return result;
  }
}