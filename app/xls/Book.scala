package xls

import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile
import org.apache.poi.ss.usermodel.Workbook
import util.Keys.XlsType._
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.FileInputStream
import com.typesafe.scalalogging.slf4j.Logging

object Book extends Logging {

  def create(file: FilePart[TemporaryFile]): Option[Workbook] = {
    file.contentType map { typ =>
      typ match {
        // Excel - 2003
        case HSSF_XLS => new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(file.ref.file)))
        // Excel 2003 -
        case XSSF_XLS => new XSSFWorkbook(new FileInputStream(file.ref.file))
      }
    }
  }
}